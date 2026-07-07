@file:Suppress("DEPRECATION", "DEPRECATION_ERROR")
@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.builders.constFalse
import org.jetbrains.kotlin.ir.builders.constTrue
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irAs
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irEqeqeq
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.builders.irGetObjectValue
import org.jetbrains.kotlin.ir.builders.irIfThen
import org.jetbrains.kotlin.ir.builders.irIfThenElse
import org.jetbrains.kotlin.ir.builders.irInt
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irSetField
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irUnit
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.Name

/**
 * Injects generated metadata registration before Fiktion fake calls.
 */
internal class FiktionGeneratedMetadataRegistrar(
    /**
     * Compiler plugin context used to reference runtime symbols and build IR.
     */
    private val pluginContext: IrPluginContext,
    /**
     * Metadata candidates collected from the current module.
     */
    private val candidates: List<FiktionGeneratedMetadataCandidate>,
    /**
     * Add-on object classes to register before fake calls.
     */
    automaticAddons: List<String>,
) : IrElementTransformerVoidWithContext() {
    /**
     * Runtime symbols needed by generated registration calls.
     */
    private val symbols = FiktionRuntimeSymbols(pluginContext)

    /**
     * Add-on objects available on the current compilation classpath.
     */
    private val automaticAddonClasses: List<IrClassSymbol> =
        automaticAddons.distinct().map { fqName ->
            requireNotNull(pluginContext.referenceClass(classId(fqName))) {
                "Fiktion automatic add-on class is not available on the compilation classpath: $fqName"
            }
        }

    /**
     * Inserts generated metadata registrations into [moduleFragment].
     */
    fun registerBeforeFakeCalls(moduleFragment: IrModuleFragment) {
        generatedRegistrar = moduleFragment.generatedRegistrar()?.symbol
        moduleFragment.transformChildrenVoid(this)
    }

    /**
     * Generated registrar function called before Fiktion fake calls.
     */
    private var generatedRegistrar: IrSimpleFunctionSymbol? = null

    override fun visitCall(expression: IrCall): IrExpression {
        expression.transformChildrenVoid(this)
        if (!expression.isFiktionFakeCall()) return expression

        val builder =
            DeclarationIrBuilder(pluginContext, currentScope!!.scope.scopeOwnerSymbol, expression.startOffset, expression.endOffset)
        return builder.irBlock(
            startOffset = expression.startOffset,
            endOffset = expression.endOffset,
            origin = null,
            resultType = expression.type,
        ) {
            generatedRegistrar?.let { registrar ->
                +builder.irCall(registrar)
            }
            expression.type.arrayTypes().forEach { (arrayType, elementType) ->
                val parent = currentScope!!.scope.scopeOwnerSymbol.owner as IrDeclarationParent
                val constructor =
                    builder
                        .arrayConstructorLambda(
                            arrayType = arrayType,
                            elementType = elementType,
                            parent = parent,
                        ).reference
                +builder.registerGeneratedArray(arrayType = arrayType, elementType = elementType, constructor = constructor)
            }
            +expression
        }
    }

    /**
     * Adds the generated registrar function to this module.
     */
    private fun IrModuleFragment.generatedRegistrar(): IrSimpleFunction? {
        if (candidates.isEmpty() && automaticAddonClasses.isEmpty()) return null
        val file = files.firstOrNull() ?: return null
        val function =
            pluginContext.irFactory.addFunction(file) {
                name = Name.identifier(FIKTION_GENERATED_REGISTRAR_NAME)
                origin = IrDeclarationOrigin.DEFINED
                visibility = DescriptorVisibilities.INTERNAL
                returnType = pluginContext.irBuiltIns.unitType
            }
        val builder = DeclarationIrBuilder(pluginContext, function.symbol)
        val initializedField = file.generatedInitializedField(builder)
        function.body =
            builder.irBlockBody {
                +builder.irIfThen(
                    pluginContext.irBuiltIns.unitType,
                    builder.irGetField(null, initializedField),
                    builder.irReturn(builder.irUnit()),
                )
                +builder.irSetField(null, initializedField, builder.irBoolean(true))
                automaticAddonClasses.forEach { addonClass ->
                    +builder.registerAddon(addonClass)
                }
                candidates.forEach { candidate ->
                    candidate.arrayTypes().forEach { (arrayType, elementType) ->
                        +builder.registerGeneratedArray(
                            arrayType = arrayType,
                            elementType = elementType,
                            constructor =
                                builder
                                    .arrayConstructorLambda(
                                        arrayType = arrayType,
                                        elementType = elementType,
                                        parent = function,
                                    ).reference,
                        )
                    }
                    +builder.registerGenerated(candidate, builder.metadata(candidate, function))
                }
            }
        return function
    }

    /**
     * Adds the generated registrar initialized field to this file.
     */
    private fun org.jetbrains.kotlin.ir.declarations.IrFile.generatedInitializedField(builder: DeclarationIrBuilder): IrField {
        val field =
            pluginContext.irFactory.buildField {
                name = Name.identifier(FIKTION_GENERATED_REGISTRAR_FIELD_NAME)
                origin = IrDeclarationOrigin.DEFINED
                visibility = DescriptorVisibilities.PRIVATE
                type = pluginContext.irBuiltIns.booleanType
                isFinal = false
                isStatic = true
            }
        field.parent = this
        field.initializer =
            pluginContext.irFactory.createExpressionBody(
                builder.startOffset,
                builder.endOffset,
                builder.irBoolean(false),
            )
        declarations.add(0, field)
        return field
    }

    /**
     * Returns whether this call targets a Fiktion fake entry point.
     */
    private fun IrCall.isFiktionFakeCall(): Boolean = symbol.owner.fqNameWhenAvailable?.asString() == FIKTION_FAKE_FUNCTION

    /**
     * Returns a registration call for [candidate].
     */
    private fun DeclarationIrBuilder.registerGenerated(
        candidate: FiktionGeneratedMetadataCandidate,
        metadata: IrExpression,
    ): IrExpression =
        irCall(symbols.registerMetadata).apply {
            setTypeArgument(0, candidate.metadataType)
            setDispatchReceiver(irGetObjectValue(symbols.fiktionCompanionType, symbols.fiktionCompanionClass))
            setRegularArgument(0, metadata)
        }

    /**
     * Returns a generated array metadata registration call.
     */
    private fun DeclarationIrBuilder.registerGeneratedArray(
        arrayType: IrType,
        elementType: IrType,
        constructor: IrExpression,
    ): IrExpression =
        irCall(symbols.registerMetadata).apply {
            setTypeArgument(0, arrayType)
            setDispatchReceiver(irGetObjectValue(symbols.fiktionCompanionType, symbols.fiktionCompanionClass))
            setRegularArgument(0, arrayMetadata(arrayType = arrayType, elementType = elementType, constructor = constructor))
        }

    /**
     * Returns an automatic add-on registration call.
     */
    private fun DeclarationIrBuilder.registerAddon(addonClass: IrClassSymbol): IrExpression =
        irCall(symbols.registerAddon).apply {
            setDispatchReceiver(irGetObjectValue(symbols.fiktionCompanionType, symbols.fiktionCompanionClass))
            setRegularArgument(0, irGetObjectValue(addonClass.owner.defaultType, addonClass))
        }

    /**
     * Returns generated type metadata for [candidate].
     */
    private fun DeclarationIrBuilder.metadata(
        candidate: FiktionGeneratedMetadataCandidate,
        lambdaParent: IrDeclarationParent,
    ): IrExpression =
        when (candidate) {
            is FiktionGeneratedEnumMetadataCandidate -> {
                enumMetadata(candidate)
            }

            is FiktionGeneratedObjectMetadataCandidate -> {
                objectMetadata(
                    candidate,
                    objectConstructorLambda(candidate, lambdaParent).reference,
                )
            }

            is FiktionGeneratedSealedMetadataCandidate -> {
                sealedMetadata(candidate)
            }

            is FiktionGeneratedSingletonMetadataCandidate -> {
                objectMetadata(
                    candidate,
                    singletonConstructorLambda(candidate, lambdaParent).reference,
                )
            }

            is FiktionGeneratedValueMetadataCandidate -> {
                valueMetadata(candidate, valueConstructorLambda(candidate, lambdaParent).reference)
            }
        }

    /**
     * Returns a `FiktionSealedMetadata<T>` expression for [candidate].
     */
    private fun DeclarationIrBuilder.sealedMetadata(candidate: FiktionGeneratedSealedMetadataCandidate): IrExpression =
        irCallConstructor(symbols.sealedMetadataConstructor, listOf(candidate.irClass.defaultType)).apply {
            setRegularArgument(0, typeOf(candidate.irClass.defaultType))
            setRegularArgument(1, sealedSubtypeList(candidate))
        }

    /**
     * Returns a `FiktionEnumMetadata<T>` expression for [candidate].
     */
    private fun DeclarationIrBuilder.enumMetadata(candidate: FiktionGeneratedEnumMetadataCandidate): IrExpression =
        irCallConstructor(symbols.enumMetadataConstructor, listOf(candidate.irClass.defaultType)).apply {
            setRegularArgument(0, typeOf(candidate.irClass.defaultType))
            setRegularArgument(1, enumEntryList(candidate))
        }

    /**
     * Returns a `FiktionValueMetadata<T>` expression for [candidate].
     */
    private fun DeclarationIrBuilder.valueMetadata(
        candidate: FiktionGeneratedValueMetadataCandidate,
        constructor: IrExpression,
    ): IrExpression =
        irCallConstructor(symbols.valueMetadataConstructor, listOf(candidate.metadataType)).apply {
            setRegularArgument(0, typeOf(candidate.metadataType))
            setRegularArgument(1, typeOf(candidate.property.type))
            setRegularArgument(2, irString(candidate.property.name))
            setRegularArgument(3, constructor)
        }

    /**
     * Returns a `FiktionArrayMetadata<T>` expression for [arrayType].
     */
    private fun DeclarationIrBuilder.arrayMetadata(
        arrayType: IrType,
        elementType: IrType,
        constructor: IrExpression,
    ): IrExpression =
        irCallConstructor(symbols.arrayMetadataConstructor, listOf(arrayType)).apply {
            setRegularArgument(0, typeOf(arrayType))
            setRegularArgument(1, typeOf(elementType))
            setRegularArgument(2, constructor)
        }

    /**
     * Returns a `FiktionObjectMetadata<T>` expression for [candidate].
     */
    private fun DeclarationIrBuilder.objectMetadata(
        candidate: FiktionGeneratedObjectMetadataCandidate,
        constructor: IrExpression,
    ): IrExpression =
        irCallConstructor(symbols.objectMetadataConstructor, listOf(candidate.type)).apply {
            setRegularArgument(0, typeOf(candidate.type))
            setRegularArgument(1, propertyList(candidate))
            setRegularArgument(2, constructor)
        }

    /**
     * Returns a `FiktionObjectMetadata<T>` expression for a singleton [candidate].
     */
    private fun DeclarationIrBuilder.objectMetadata(
        candidate: FiktionGeneratedSingletonMetadataCandidate,
        constructor: IrExpression,
    ): IrExpression =
        irCallConstructor(symbols.objectMetadataConstructor, listOf(candidate.irClass.defaultType)).apply {
            setRegularArgument(0, typeOf(candidate.irClass.defaultType))
            setRegularArgument(1, emptyPropertyList())
            setRegularArgument(2, constructor)
        }

    /**
     * Returns a `typeOf<T>()` expression.
     */
    private fun DeclarationIrBuilder.typeOf(type: IrType): IrExpression =
        irCall(symbols.typeOf).apply {
            setTypeArgument(0, type)
        }

    /**
     * Returns a list of generated `FiktionObjectProperty` values for [candidate].
     */
    private fun DeclarationIrBuilder.propertyList(candidate: FiktionGeneratedObjectMetadataCandidate): IrExpression {
        val propertyType = symbols.objectPropertyType
        val properties =
            candidate.properties.map { property ->
                irCallConstructor(symbols.objectPropertyConstructor, emptyList()).apply {
                    setRegularArgument(0, irString(property.name))
                    setRegularArgument(1, typeOf(property.type))
                    setRegularArgument(2, irBoolean(property.hasDefault))
                }
            }

        return irCall(symbols.listOf).apply {
            setTypeArgument(0, propertyType)
            setRegularArgument(0, irVararg(propertyType, properties))
        }
    }

    /**
     * Returns an empty generated `FiktionObjectProperty` list.
     */
    private fun DeclarationIrBuilder.emptyPropertyList(): IrExpression =
        irCall(symbols.listOf).apply {
            setTypeArgument(0, symbols.objectPropertyType)
            setRegularArgument(0, irVararg(symbols.objectPropertyType, emptyList()))
        }

    /**
     * Returns a list of generated enum entries for [candidate].
     */
    private fun DeclarationIrBuilder.enumEntryList(candidate: FiktionGeneratedEnumMetadataCandidate): IrExpression =
        irCall(symbols.listOf).apply {
            setTypeArgument(0, candidate.irClass.defaultType)
            setRegularArgument(
                0,
                irVararg(
                    candidate.irClass.defaultType,
                    candidate.entries.map { entry -> enumEntry(entry, candidate.irClass.defaultType) },
                ),
            )
        }

    /**
     * Returns a list of generated sealed subtype KTypes for [candidate].
     */
    private fun DeclarationIrBuilder.sealedSubtypeList(candidate: FiktionGeneratedSealedMetadataCandidate): IrExpression =
        irCall(symbols.listOf).apply {
            setTypeArgument(0, symbols.kType)
            setRegularArgument(
                0,
                irVararg(
                    symbols.kType,
                    candidate.subtypes.map { subtype -> typeOf(subtype.defaultType) },
                ),
            )
        }

    /**
     * Returns an enum entry expression.
     */
    private fun DeclarationIrBuilder.enumEntry(
        entry: IrEnumEntry,
        type: IrType,
    ): IrExpression =
        irGetEnumValueConstructor.newInstance(
            irElementConstructorIndicator,
            startOffset,
            endOffset,
            type,
            entry.symbol,
        ) as IrExpression

    /**
     * Returns the generated constructor lambda used by `FiktionObjectMetadata`.
     */
    private fun DeclarationIrBuilder.objectConstructorLambda(
        candidate: FiktionGeneratedObjectMetadataCandidate,
        parent: IrDeclarationParent,
    ): ConstructorLambda {
        val classType = candidate.type
        val argumentsType = symbols.objectArgumentListType
        val functionType = pluginContext.irBuiltIns.functionN(1).typeWith(argumentsType, classType)
        val function = buildLocalLambda(parent = parent, returnType = classType)
        val arguments = function.addValueParameter("values", argumentsType)
        function.body =
            DeclarationIrBuilder(pluginContext, function.symbol).irBlockBody {
                +irReturn(constructorCall(candidate, arguments))
            }

        return ConstructorLambda(
            function = function,
            reference = functionExpression(functionType, function),
        )
    }

    /**
     * Returns the generated constructor lambda used by singleton `FiktionObjectMetadata`.
     */
    private fun DeclarationIrBuilder.singletonConstructorLambda(
        candidate: FiktionGeneratedSingletonMetadataCandidate,
        parent: IrDeclarationParent,
    ): ConstructorLambda {
        val classType = candidate.irClass.defaultType
        val argumentsType = symbols.objectArgumentListType
        val functionType = pluginContext.irBuiltIns.functionN(1).typeWith(argumentsType, classType)
        val function = buildLocalLambda(parent = parent, returnType = classType)
        function.addValueParameter("values", argumentsType)
        function.body =
            DeclarationIrBuilder(pluginContext, function.symbol).irBlockBody {
                +irReturn(irGetObjectValue(classType, candidate.irClass.symbol))
            }

        return ConstructorLambda(
            function = function,
            reference = functionExpression(functionType, function),
        )
    }

    /**
     * Returns the generated constructor lambda used by `FiktionValueMetadata`.
     */
    private fun DeclarationIrBuilder.valueConstructorLambda(
        candidate: FiktionGeneratedValueMetadataCandidate,
        parent: IrDeclarationParent,
    ): ConstructorLambda {
        val classType = candidate.metadataType
        val valueType = pluginContext.irBuiltIns.anyNType
        val functionType = pluginContext.irBuiltIns.functionN(1).typeWith(valueType, classType)
        val function = buildLocalLambda(parent = parent, returnType = classType)
        val value = function.addValueParameter("value", valueType)
        function.body =
            DeclarationIrBuilder(pluginContext, function.symbol).irBlockBody {
                +irReturn(valueConstructorCall(candidate, value))
            }

        return ConstructorLambda(
            function = function,
            reference = functionExpression(functionType, function),
        )
    }

    /**
     * Returns the generated constructor lambda used by `FiktionArrayMetadata`.
     */
    private fun DeclarationIrBuilder.arrayConstructorLambda(
        arrayType: IrType,
        elementType: IrType,
        parent: IrDeclarationParent,
    ): ConstructorLambda {
        val elementsType = symbols.anyListType
        val functionType = pluginContext.irBuiltIns.functionN(1).typeWith(elementsType, arrayType)
        val function = buildLocalLambda(parent = parent, returnType = arrayType)
        val elements = function.addValueParameter("elements", elementsType)
        function.body =
            DeclarationIrBuilder(pluginContext, function.symbol).irBlockBody {
                +irReturn(
                    irCall(symbols.toTypedArray).apply {
                        setTypeArgument(0, elementType)
                        setExtensionReceiver(
                            irAs(
                                irGet(elements),
                                pluginContext.irBuiltIns.collectionClass.typeWith(elementType),
                            ),
                        )
                    },
                )
            }

        return ConstructorLambda(
            function = function,
            reference = functionExpression(functionType, function),
        )
    }

    /**
     * Returns an IR function expression for [function].
     */
    private fun DeclarationIrBuilder.functionExpression(
        functionType: IrType,
        function: IrSimpleFunction,
    ): IrExpression =
        irFunctionExpressionConstructor.newInstance(
            irElementConstructorIndicator,
            startOffset,
            endOffset,
            functionType,
            IrStatementOrigin.LAMBDA,
            function,
        ) as IrExpression

    /**
     * Builds a local anonymous function used by generated constructor lambdas.
     */
    private fun buildLocalLambda(
        parent: IrDeclarationParent,
        returnType: IrType,
    ): IrSimpleFunction =
        pluginContext.irFactory
            .buildFun {
                name = Name.special("<anonymous>")
                origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
                visibility = DescriptorVisibilities.LOCAL
                this.returnType = returnType
            }.also { function ->
                function.parent = parent
            }

    /**
     * Returns a constructor call for [candidate] reading generated values from [arguments].
     */
    private fun DeclarationIrBuilder.constructorCall(
        candidate: FiktionGeneratedObjectMetadataCandidate,
        arguments: IrValueParameter,
    ): IrExpression =
        constructorCall(
            candidate = candidate,
            arguments = arguments,
            defaultParameterIndexes = emptySet(),
            pendingDefaultParameterIndexes =
                candidate.properties
                    .mapIndexedNotNull { index, property -> index.takeIf { property.hasDefault } },
        )

    /**
     * Returns a constructor call expression with branches for remaining defaultable parameters.
     */
    private fun DeclarationIrBuilder.constructorCall(
        candidate: FiktionGeneratedObjectMetadataCandidate,
        arguments: IrValueParameter,
        defaultParameterIndexes: Set<Int>,
        pendingDefaultParameterIndexes: List<Int>,
    ): IrExpression {
        val parameterIndex =
            pendingDefaultParameterIndexes.firstOrNull() ?: return constructorCall(
                candidate = candidate,
                arguments = arguments,
                defaultParameterIndexes = defaultParameterIndexes,
            )

        val remaining = pendingDefaultParameterIndexes.drop(1)
        return irIfThenElse(
            type = candidate.irClass.defaultType,
            condition = generatedArgumentUsesDefault(arguments, parameterIndex),
            thenPart =
                constructorCall(
                    candidate = candidate,
                    arguments = arguments,
                    defaultParameterIndexes = defaultParameterIndexes + parameterIndex,
                    pendingDefaultParameterIndexes = remaining,
                ),
            elsePart =
                constructorCall(
                    candidate = candidate,
                    arguments = arguments,
                    defaultParameterIndexes = defaultParameterIndexes,
                    pendingDefaultParameterIndexes = remaining,
                ),
        )
    }

    /**
     * Returns a constructor call for a resolved default argument combination.
     */
    private fun DeclarationIrBuilder.constructorCall(
        candidate: FiktionGeneratedObjectMetadataCandidate,
        arguments: IrValueParameter,
        defaultParameterIndexes: Set<Int>,
    ): IrExpression =
        irCallConstructor(candidate.constructor.symbol, candidate.type.constructorTypeArguments()).apply {
            candidate.properties.forEachIndexed { index, property ->
                if (index !in defaultParameterIndexes) {
                    setRegularArgument(index, generatedArgument(arguments, index, property.type))
                }
            }
        }

    /**
     * Returns a value-class constructor call reading the generated underlying [value].
     */
    private fun DeclarationIrBuilder.valueConstructorCall(
        candidate: FiktionGeneratedValueMetadataCandidate,
        value: IrValueParameter,
    ): IrExpression =
        irCallConstructor(candidate.constructor.symbol, candidate.metadataType.constructorTypeArguments()).apply {
            setRegularArgument(0, irAs(irGet(value), candidate.property.type))
        }

    /**
     * Returns whether generated constructor argument at [index] asks to use the Kotlin default value.
     */
    private fun DeclarationIrBuilder.generatedArgumentUsesDefault(
        arguments: IrValueParameter,
        index: Int,
    ): IrExpression =
        irEqeqeq(
            generatedObjectArgument(arguments = arguments, index = index),
            irGetObjectValue(symbols.objectDefaultType, symbols.objectDefaultClass),
        )

    /**
     * Returns a generated constructor argument read from [arguments] at [index] and cast to [type].
     */
    private fun DeclarationIrBuilder.generatedArgument(
        arguments: IrValueParameter,
        index: Int,
        type: IrType,
    ): IrExpression {
        val value =
            irCall(symbols.objectValueGetter).apply {
                setDispatchReceiver(
                    irAs(
                        generatedObjectArgument(arguments = arguments, index = index),
                        symbols.objectValueType,
                    ),
                )
            }
        return irAs(value, type)
    }

    /**
     * Returns a generated constructor argument from [arguments] at [index].
     */
    private fun DeclarationIrBuilder.generatedObjectArgument(
        arguments: IrValueParameter,
        index: Int,
    ): IrExpression =
        irCall(symbols.listGet).apply {
            setDispatchReceiver(irGet(arguments))
            setRegularArgument(0, irInt(index))
        }

    /**
     * Returns a boolean constant.
     */
    private fun DeclarationIrBuilder.irBoolean(value: Boolean): IrExpression =
        if (value) {
            context.constTrue(startOffset, endOffset)
        } else {
            context.constFalse(startOffset, endOffset)
        }
}

/**
 * Internal IR constructor indicator instance used by IR expression implementations.
 */
private val irElementConstructorIndicator: Any =
    Class
        .forName("org.jetbrains.kotlin.ir.util.IrElementConstructorIndicator")
        .getField("INSTANCE")
        .get(null)

/**
 * Constructor for `IrFunctionExpressionImpl`.
 */
private val irFunctionExpressionConstructor =
    Class
        .forName("org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl")
        .constructors
        .single { constructor -> constructor.parameterCount == 6 }

/**
 * Constructor for `IrGetEnumValueImpl`.
 */
private val irGetEnumValueConstructor =
    Class
        .forName("org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl")
        .constructors
        .single { constructor -> constructor.parameterCount == 5 }

/**
 * Sets the type argument at [index].
 */
private fun IrMemberAccessExpression<*>.setTypeArgument(
    index: Int,
    type: IrType,
) {
    typeArguments[index] = type
}

/**
 * Sets the regular value argument at [index].
 */
private fun IrMemberAccessExpression<*>.setRegularArgument(
    index: Int,
    expression: IrExpression,
) {
    arguments[regularParameters[index]] = expression
}

/**
 * Returns array metadata types needed by this type and its type arguments.
 */
private fun IrType.arrayTypes(): List<Pair<IrType, IrType>> {
    val typeArgumentArrays =
        (this as? IrSimpleType)
            ?.arguments
            ?.mapNotNull { argument -> argument.typeOrNull }
            ?.flatMap { argumentType -> argumentType.arrayTypes() }
            .orEmpty()
    val elementType = arrayElementTypeOrNull() ?: return typeArgumentArrays
    return typeArgumentArrays + (this to elementType)
}

/**
 * Returns the element type when this type is `Array<T>`.
 */
private fun IrType.arrayElementTypeOrNull(): IrType? {
    if (classOrNull?.owner?.fqNameWhenAvailable?.asString() != "kotlin.Array") return null
    return (this as? IrSimpleType)?.arguments?.singleOrNull()?.typeOrNull
}

/**
 * Returns class type arguments usable for constructor calls of this type.
 */
private fun IrType.constructorTypeArguments(): List<IrType> {
    val typeParameters = classOrNull?.owner?.typeParameters ?: return emptyList()
    return (this as? IrSimpleType)
        ?.arguments
        ?.take(typeParameters.size)
        ?.mapNotNull { argument -> argument.typeOrNull }
        .orEmpty()
}

/**
 * Returns array metadata types needed by this generated metadata candidate.
 */
private fun FiktionGeneratedMetadataCandidate.arrayTypes(): List<Pair<IrType, IrType>> =
    when (this) {
        is FiktionGeneratedObjectMetadataCandidate -> properties.flatMap { property -> property.type.arrayTypes() }
        is FiktionGeneratedValueMetadataCandidate -> property.type.arrayTypes()
        else -> emptyList()
    }

/**
 * Returns the type that should be registered for this generated metadata candidate.
 */
private val FiktionGeneratedMetadataCandidate.metadataType: IrType
    get() =
        when (this) {
            is FiktionGeneratedObjectMetadataCandidate -> type
            is FiktionGeneratedValueMetadataCandidate -> type
            else -> irClass.defaultType
        }

/**
 * Sets the dispatch receiver value.
 */
private fun IrMemberAccessExpression<*>.setDispatchReceiver(expression: IrExpression) {
    arguments[dispatchReceiverParameter] = expression
}

/**
 * Sets the extension receiver value.
 */
private fun IrMemberAccessExpression<*>.setExtensionReceiver(expression: IrExpression) {
    arguments[extensionReceiverParameter] = expression
}

/**
 * Regular parameters in declaration order.
 */
private val IrMemberAccessExpression<*>.regularParameters: List<IrValueParameter>
    get() = functionSymbol.owner.parameters.filter { parameter -> parameter.kind == IrParameterKind.Regular }

/**
 * Dispatch receiver parameter.
 */
private val IrMemberAccessExpression<*>.dispatchReceiverParameter: IrValueParameter
    get() = functionSymbol.owner.parameters.single { parameter -> parameter.kind == IrParameterKind.DispatchReceiver }

/**
 * Extension receiver parameter.
 */
private val IrMemberAccessExpression<*>.extensionReceiverParameter: IrValueParameter
    get() = functionSymbol.owner.parameters.single { parameter -> parameter.kind == IrParameterKind.ExtensionReceiver }

/**
 * Function symbol targeted by this member access expression.
 */
private val IrMemberAccessExpression<*>.functionSymbol: IrFunctionSymbol
    get() = symbol as IrFunctionSymbol
