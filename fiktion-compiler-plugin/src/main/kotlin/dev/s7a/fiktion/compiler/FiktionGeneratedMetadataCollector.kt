package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.AbstractIrTypeSubstitutor
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.makeNotNull
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.isNullable
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid

/**
 * Collects classes that can receive generated Fiktion metadata.
 */
internal class FiktionGeneratedMetadataCollector {
    /**
     * Returns metadata candidates found in [moduleFragment].
     */
    fun collect(moduleFragment: IrModuleFragment): List<FiktionGeneratedMetadataCandidate> {
        val candidates = mutableListOf<FiktionGeneratedMetadataCandidate>()

        moduleFragment.accept(
            object : IrVisitorVoid() {
                override fun visitElement(element: IrElement) {
                    element.acceptChildren(this, null)
                }

                @OptIn(UnsafeDuringIrConstructionAPI::class)
                override fun visitCall(expression: IrCall) {
                    expression
                        .factoryMetadataCandidate()
                        ?.addCandidate(candidates, visited = mutableSetOf())
                    expression
                        .takeIf { call -> call.isFiktionFakeCall() }
                        ?.type
                        ?.addCandidates(candidates, visited = mutableSetOf())
                    super.visitCall(expression)
                }
            },
            null,
        )

        return candidates
            .groupBy { candidate -> candidate.id }
            .values
            .map { matchingCandidates ->
                matchingCandidates.firstOrNull { candidate -> candidate is FiktionGeneratedObjectFactoryMetadataCandidate }
                    ?: matchingCandidates.first()
            }
    }

    /**
     * Adds this type and type arguments when they map to supported classes.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrType.addCandidates(
        candidates: MutableList<FiktionGeneratedMetadataCandidate>,
        visited: MutableSet<String>,
    ) {
        classOrNull?.owner?.addCandidate(this, candidates, visited)
        (this as? IrSimpleType)
            ?.arguments
            ?.mapNotNull { argument -> argument.typeOrNull }
            ?.forEach { type -> type.addCandidates(candidates, visited) }
    }

    /**
     * Adds this class and sealed subtype leaves when they are supported.
     */
    private fun IrClass.addCandidate(
        type: IrType,
        candidates: MutableList<FiktionGeneratedMetadataCandidate>,
        visited: MutableSet<String>,
    ) {
        val candidate = toCandidate(type) ?: return
        candidate.addCandidate(candidates, visited)
    }

    /**
     * Adds this candidate and metadata candidates required by its referenced types.
     */
    private fun FiktionGeneratedMetadataCandidate.addCandidate(
        candidates: MutableList<FiktionGeneratedMetadataCandidate>,
        visited: MutableSet<String>,
    ) {
        if (!visited.add(id)) return
        candidates.add(this)
        referencedClasses().forEach { irClass ->
            irClass.addCandidate(irClass.defaultType, candidates, visited)
        }
    }

    /**
     * Returns classes that generated metadata for this candidate needs at runtime.
     */
    private fun FiktionGeneratedMetadataCandidate.referencedClasses(): List<IrClass> =
        when (this) {
            is FiktionGeneratedObjectMetadataCandidate -> {
                properties.flatMap { property ->
                    property.type.referencedClasses()
                }
            }

            is FiktionGeneratedObjectFactoryMetadataCandidate -> {
                properties.flatMap { property ->
                    property.type.referencedClasses()
                }
            }

            is FiktionGeneratedValueMetadataCandidate -> {
                property.type.referencedClasses()
            }

            is FiktionGeneratedSealedMetadataCandidate -> {
                subtypes
            }

            is FiktionGeneratedEnumMetadataCandidate,
            is FiktionGeneratedSingletonMetadataCandidate,
            -> {
                emptyList()
            }
        }

    /**
     * Returns classes referenced by this type and type arguments.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrType.referencedClasses(): List<IrClass> =
        listOfNotNull(classOrNull?.owner) +
            (
                (this as? IrSimpleType)
                    ?.arguments
                    ?.mapNotNull { argument -> argument.typeOrNull }
                    ?.flatMap { type -> type.referencedClasses() }
                    ?: emptyList()
            )

    /**
     * Returns a generated metadata candidate for this class, or `null` when the class is outside the supported shape.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrClass.toCandidate(type: IrType): FiktionGeneratedMetadataCandidate? {
        if (isExpect || isInner) return null
        val className = fqNameWhenAvailable?.asString().orEmpty()
        if (className.isBlank()) return null
        val candidateType = (type.takeIf { it.classOrNull?.owner == this } ?: defaultType).makeNotNull()

        if (modality == Modality.SEALED) {
            return FiktionGeneratedSealedMetadataCandidate(
                irClass = this,
                className = className,
                subtypes = concreteSealedSubtypes(),
            )
        }

        if (kind == ClassKind.ENUM_CLASS) {
            return FiktionGeneratedEnumMetadataCandidate(
                irClass = this,
                className = className,
                entries = declarations.filterIsInstance<IrEnumEntry>(),
            )
        }

        if (kind == ClassKind.OBJECT) {
            return FiktionGeneratedSingletonMetadataCandidate(
                irClass = this,
                className = className,
            )
        }

        if (kind != ClassKind.CLASS || modality == Modality.ABSTRACT) return null
        val valueClass = isValue || valueClassRepresentation != null
        val constructor = declarations.filterIsInstance<IrConstructor>().firstOrNull { constructor -> constructor.isPrimary } ?: return null
        if (constructor.isHiddenConstructor()) return null
        if (constructor.hasUnsupportedParameters()) return null
        val parameters = constructor.parameters
        if (valueClass && parameters.size != 1) return null
        val substitutor = (candidateType as? IrSimpleType)?.let { type -> AbstractIrTypeSubstitutor.forType(type) }
        val properties =
            parameters.map { parameter ->
                val type = substitutor?.substitute(parameter.type) ?: parameter.type
                FiktionGeneratedMetadataPropertyCandidate(
                    parameter = parameter,
                    type = type,
                    name = parameter.name.asString(),
                    hasDefault = parameter.defaultValue != null,
                )
            }

        return if (valueClass) {
            FiktionGeneratedValueMetadataCandidate(
                irClass = this,
                type = candidateType,
                constructor = constructor,
                className = className,
                property = properties.single(),
            )
        } else {
            FiktionGeneratedObjectMetadataCandidate(
                irClass = this,
                type = candidateType,
                constructor = constructor,
                className = className,
                properties = properties,
            )
        }
    }

    /**
     * Returns concrete sealed subtype leaves for this class.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrClass.concreteSealedSubtypes(): List<IrClass> =
        sealedSubclasses.flatMap { symbol ->
            val subtype = symbol.owner
            if (subtype.modality == Modality.SEALED) {
                subtype.concreteSealedSubtypes()
            } else {
                listOf(subtype)
            }
        }

    /**
     * Returns whether this constructor should stay hidden from generated metadata.
     */
    private fun IrConstructor.isHiddenConstructor(): Boolean =
        visibility == DescriptorVisibilities.PRIVATE || visibility == DescriptorVisibilities.PROTECTED

    /**
     * Returns whether this constructor contains parameters generated metadata cannot call safely.
     */
    private fun IrConstructor.hasUnsupportedParameters(): Boolean =
        parameters.any { parameter ->
            parameter.kind != IrParameterKind.Regular || parameter.varargElementType != null
        }

    /**
     * Returns generated metadata for an explicit `constructsBy` declaration.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrCall.factoryMetadataCandidate(): FiktionGeneratedObjectFactoryMetadataCandidate? {
        if (symbol.owner.fqNameWhenAvailable?.asString() != FIKTION_CONSTRUCTS_BY_FUNCTION) return null
        val factoryParameter = symbol.owner.parameters.single { parameter -> parameter.kind == IrParameterKind.Regular }
        val factory =
            arguments[factoryParameter]
                ?.factoryFunctionReference()
                ?.symbol
                ?.owner as? IrSimpleFunction ?: return null
        if (factory.hasUnsupportedFactoryParameters()) return null
        val type = typeArguments.singleOrNull() ?: factory.returnType
        if (factory.returnType.isNullable() && !type.isNullable()) return null
        val irClass = type.classOrNull?.owner ?: return null
        if (irClass.kind != ClassKind.CLASS || irClass.modality == Modality.ABSTRACT) return null
        val className = irClass.fqNameWhenAvailable?.asString().orEmpty()
        if (className.isBlank()) return null
        val substitutor = (type as? IrSimpleType)?.let { type -> AbstractIrTypeSubstitutor.forType(type) }
        val properties =
            factory.parameters
                .filter { parameter -> parameter.kind == IrParameterKind.Regular }
                .map { parameter ->
                    val parameterType = substitutor?.substitute(parameter.type) ?: parameter.type
                    FiktionGeneratedMetadataPropertyCandidate(
                        parameter = parameter,
                        type = parameterType,
                        name = parameter.name.asString(),
                        hasDefault = parameter.defaultValue != null,
                    )
                }
        return FiktionGeneratedObjectFactoryMetadataCandidate(
            irClass = irClass,
            type = type,
            factory = factory,
            className = className,
            properties = properties,
        )
    }

    /**
     * Returns a callable reference passed directly or through a local typed value.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrExpression.factoryFunctionReference(): IrFunctionReference? =
        when (this) {
            is IrFunctionReference -> this
            is IrGetValue -> (symbol.owner as? IrVariable)?.initializer?.factoryFunctionReference()
            is IrTypeOperatorCall -> argument.factoryFunctionReference()
            else -> null
        }

    /**
     * Returns whether this factory function contains parameters generated metadata cannot call safely.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrSimpleFunction.hasUnsupportedFactoryParameters(): Boolean =
        parameters.any { parameter ->
            when (parameter.kind) {
                IrParameterKind.Regular -> {
                    parameter.varargElementType != null
                }

                IrParameterKind.DispatchReceiver -> {
                    parameter.type
                        .classOrNull
                        ?.owner
                        ?.kind != ClassKind.OBJECT
                }

                else -> {
                    true
                }
            }
        }

    /**
     * Returns whether this call targets a Fiktion fake entry point.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrCall.isFiktionFakeCall(): Boolean = symbol.owner.fqNameWhenAvailable?.asString() == FIKTION_FAKE_FUNCTION

    /**
     * Returns the uniqueness key for generated metadata candidates.
     */
    private val FiktionGeneratedMetadataCandidate.id: String
        get() =
            when (this) {
                is FiktionGeneratedObjectMetadataCandidate -> type.render()
                is FiktionGeneratedObjectFactoryMetadataCandidate -> type.render()
                is FiktionGeneratedValueMetadataCandidate -> type.render()
                else -> className
            }
}
