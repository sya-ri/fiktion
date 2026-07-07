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
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.AbstractIrTypeSubstitutor
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
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
                        .takeIf { call -> call.isFiktionFakeCall() }
                        ?.type
                        ?.addCandidates(candidates, visited = mutableSetOf())
                    super.visitCall(expression)
                }
            },
            null,
        )

        return candidates.distinctBy { candidate -> candidate.id }
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
        if (!visited.add(candidate.id)) return
        candidates.add(candidate)
        candidate.referencedClasses().forEach { irClass ->
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
        val candidateType = type.takeIf { it.classOrNull?.owner == this } ?: defaultType

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
                is FiktionGeneratedValueMetadataCandidate -> type.render()
                else -> className
            }
}
