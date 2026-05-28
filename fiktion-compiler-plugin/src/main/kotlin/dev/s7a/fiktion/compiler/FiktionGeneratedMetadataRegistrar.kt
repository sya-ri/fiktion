@file:Suppress("DEPRECATION", "DEPRECATION_ERROR")
@file:OptIn(org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI::class)

package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.builders.constFalse
import org.jetbrains.kotlin.ir.builders.constTrue
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irAs
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObjectValue
import org.jetbrains.kotlin.ir.builders.irIfThenElse
import org.jetbrains.kotlin.ir.builders.irInt
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
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
) : IrElementTransformerVoidWithContext() {
    /**
     * Runtime symbols needed by generated registration calls.
     */
    private val symbols = FiktionRuntimeSymbols(pluginContext)

    /**
     * Inserts generated metadata registrations into [moduleFragment].
     */
    fun registerBeforeFakeCalls(moduleFragment: IrModuleFragment) {
        if (candidates.isEmpty()) return
        moduleFragment.transformChildrenVoid(this)
    }

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
            candidates.forEach { candidate ->
                val lambda =
                    if (candidate.isValueClass) {
                        builder.valueConstructorLambda(candidate)
                    } else {
                        builder.objectConstructorLambda(candidate)
                    }
                +builder.registerGenerated(candidate, builder.metadata(candidate, lambda.reference))
            }
            +expression
        }
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
        irCall(symbols.registerGeneratedMetadata).apply {
            setTypeArgument(0, candidate.irClass.defaultType)
            setDispatchReceiver(irGetObjectValue(symbols.fiktionCompanionType, symbols.fiktionCompanionClass))
            setRegularArgument(0, metadata)
        }

    /**
     * Returns generated type metadata for [candidate].
     */
    private fun DeclarationIrBuilder.metadata(
        candidate: FiktionGeneratedMetadataCandidate,
        constructor: IrExpression,
    ): IrExpression =
        if (candidate.isValueClass) {
            valueMetadata(candidate, constructor)
        } else {
            objectMetadata(candidate, constructor)
        }

    /**
     * Returns a `FiktionValueMetadata<T>` expression for [candidate].
     */
    private fun DeclarationIrBuilder.valueMetadata(
        candidate: FiktionGeneratedMetadataCandidate,
        constructor: IrExpression,
    ): IrExpression =
        irCallConstructor(symbols.valueMetadataConstructor, listOf(candidate.irClass.defaultType)).apply {
            setRegularArgument(0, typeOf(candidate.irClass.defaultType))
            setRegularArgument(1, typeOf(candidate.singleProperty.parameter.type))
            setRegularArgument(2, constructor)
        }

    /**
     * Returns a `FiktionObjectMetadata<T>` expression for [candidate].
     */
    private fun DeclarationIrBuilder.objectMetadata(
        candidate: FiktionGeneratedMetadataCandidate,
        constructor: IrExpression,
    ): IrExpression =
        irCallConstructor(symbols.objectMetadataConstructor, listOf(candidate.irClass.defaultType)).apply {
            setRegularArgument(0, typeOf(candidate.irClass.defaultType))
            setRegularArgument(1, propertyList(candidate))
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
    private fun DeclarationIrBuilder.propertyList(candidate: FiktionGeneratedMetadataCandidate): IrExpression {
        val propertyType = symbols.objectPropertyType
        val properties =
            candidate.properties.map { property ->
                irCallConstructor(symbols.objectPropertyConstructor, emptyList()).apply {
                    setRegularArgument(0, irString(property.name))
                    setRegularArgument(1, typeOf(property.parameter.type))
                    setRegularArgument(2, irBoolean(property.hasDefault))
                }
            }

        return irCall(symbols.listOf).apply {
            setTypeArgument(0, propertyType)
            setRegularArgument(0, irVararg(propertyType, properties))
        }
    }

    /**
     * Returns the generated constructor lambda used by `FiktionObjectMetadata`.
     */
    private fun DeclarationIrBuilder.objectConstructorLambda(candidate: FiktionGeneratedMetadataCandidate): ConstructorLambda {
        val classType = candidate.irClass.defaultType
        val argumentsType = symbols.objectArgumentListType
        val functionType = pluginContext.irBuiltIns.functionN(1).typeWith(argumentsType, classType)
        val function =
            pluginContext.irFactory.buildFun {
                name = Name.special("<anonymous>")
                origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
                visibility = DescriptorVisibilities.LOCAL
                returnType = classType
            }
        function.parent = requireNotNull(currentDeclarationParent)
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
     * Returns the generated constructor lambda used by `FiktionValueMetadata`.
     */
    private fun DeclarationIrBuilder.valueConstructorLambda(candidate: FiktionGeneratedMetadataCandidate): ConstructorLambda {
        val classType = candidate.irClass.defaultType
        val valueType = pluginContext.irBuiltIns.anyNType
        val functionType = pluginContext.irBuiltIns.functionN(1).typeWith(valueType, classType)
        val function =
            pluginContext.irFactory.buildFun {
                name = Name.special("<anonymous>")
                origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
                visibility = DescriptorVisibilities.LOCAL
                returnType = classType
            }
        function.parent = requireNotNull(currentDeclarationParent)
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
     * Returns a constructor call for [candidate] reading generated values from [arguments].
     */
    private fun DeclarationIrBuilder.constructorCall(
        candidate: FiktionGeneratedMetadataCandidate,
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
        candidate: FiktionGeneratedMetadataCandidate,
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
        candidate: FiktionGeneratedMetadataCandidate,
        arguments: IrValueParameter,
        defaultParameterIndexes: Set<Int>,
    ): IrExpression =
        irCallConstructor(candidate.constructor.symbol, emptyList()).apply {
            candidate.properties.forEachIndexed { index, property ->
                if (index !in defaultParameterIndexes) {
                    setRegularArgument(index, generatedArgument(arguments, index, property.parameter.type))
                }
            }
        }

    /**
     * Returns a value-class constructor call reading the generated underlying [value].
     */
    private fun DeclarationIrBuilder.valueConstructorCall(
        candidate: FiktionGeneratedMetadataCandidate,
        value: IrValueParameter,
    ): IrExpression =
        irCallConstructor(candidate.constructor.symbol, emptyList()).apply {
            setRegularArgument(0, irAs(irGet(value), candidate.singleProperty.parameter.type))
        }

    /**
     * Returns whether generated constructor argument at [index] asks to use the Kotlin default value.
     */
    private fun DeclarationIrBuilder.generatedArgumentUsesDefault(
        arguments: IrValueParameter,
        index: Int,
    ): IrExpression =
        irCall(symbols.generatedObjectArgumentUsesDefault).apply {
            setRegularArgument(0, irGet(arguments))
            setRegularArgument(1, irInt(index))
        }

    /**
     * Returns a generated constructor argument read from [arguments] at [index] and cast to [type].
     */
    private fun DeclarationIrBuilder.generatedArgument(
        arguments: IrValueParameter,
        index: Int,
        type: IrType,
    ): IrExpression {
        val value =
            irCall(symbols.generatedObjectArgumentValue).apply {
                setRegularArgument(0, irGet(arguments))
                setRegularArgument(1, irInt(index))
            }
        return irAs(value, type)
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

    /**
     * Runtime symbol references used by the registrar.
     */
    private class FiktionRuntimeSymbols(
        /**
         * Compiler plugin context used for symbol lookup.
         */
        pluginContext: IrPluginContext,
    ) {
        /**
         * `Fiktion.Companion` class.
         */
        val fiktionCompanionClass: IrClassSymbol =
            requireNotNull(
                pluginContext.referenceClass(
                    ClassId(
                        packageFqName = FqName(FIKTION_PACKAGE),
                        relativeClassName = FqName("Fiktion.Companion"),
                        isLocal = false,
                    ),
                ),
            )

        /**
         * `Fiktion.Companion` type.
         */
        val fiktionCompanionType: IrType = fiktionCompanionClass.owner.defaultType

        /**
         * `Fiktion.registerGeneratedMetadata` companion function.
         */
        val registerGeneratedMetadata: IrSimpleFunctionSymbol =
            fiktionCompanionClass.owner.declarations
                .filterIsInstance<IrSimpleFunction>()
                .single { function -> function.name == Name.identifier("registerGeneratedMetadata") }
                .symbol

        /**
         * `generatedObjectArgumentValue` top-level function.
         */
        val generatedObjectArgumentValue: IrSimpleFunctionSymbol =
            pluginContext.referenceFunctions(callableId(FIKTION_PACKAGE, "generatedObjectArgumentValue")).single()

        /**
         * `generatedObjectArgumentUsesDefault` top-level function.
         */
        val generatedObjectArgumentUsesDefault: IrSimpleFunctionSymbol =
            pluginContext.referenceFunctions(callableId(FIKTION_PACKAGE, "generatedObjectArgumentUsesDefault")).single()

        /**
         * `typeOf` top-level function.
         */
        val typeOf: IrSimpleFunctionSymbol = pluginContext.referenceFunctions(callableId("kotlin.reflect", "typeOf")).single()

        /**
         * `listOf` top-level function.
         */
        val listOf: IrSimpleFunctionSymbol =
            pluginContext
                .referenceFunctions(callableId("kotlin.collections", "listOf"))
                .single { function ->
                    function.owner.parameters.any { parameter ->
                        parameter.kind == IrParameterKind.Regular && parameter.varargElementType != null
                    }
                }

        /**
         * `FiktionObjectMetadata` constructor.
         */
        val objectMetadataConstructor: IrConstructorSymbol =
            pluginContext.referenceConstructors(classId("$FIKTION_PACKAGE.FiktionObjectMetadata")).single()

        /**
         * `FiktionValueMetadata` constructor.
         */
        val valueMetadataConstructor: IrConstructorSymbol =
            pluginContext.referenceConstructors(classId("$FIKTION_PACKAGE.FiktionValueMetadata")).single()

        /**
         * `FiktionObjectProperty` constructor.
         */
        val objectPropertyConstructor: IrConstructorSymbol =
            pluginContext.referenceConstructors(classId("$FIKTION_PACKAGE.FiktionObjectProperty")).first()

        /**
         * `FiktionObjectProperty` type.
         */
        val objectPropertyType: IrType =
            requireNotNull(pluginContext.referenceClass(classId("$FIKTION_PACKAGE.FiktionObjectProperty"))).owner.defaultType

        /**
         * `FiktionObjectArgument` type.
         */
        val objectArgumentType: IrType =
            requireNotNull(pluginContext.referenceClass(classId("$FIKTION_PACKAGE.FiktionObjectArgument"))).owner.defaultType

        /**
         * `List<FiktionObjectArgument>` type used by constructor lambdas.
         */
        val objectArgumentListType: IrType = pluginContext.irBuiltIns.listClass.typeWith(objectArgumentType)
    }

    /**
     * Local constructor function and callable value passed to runtime metadata.
     */
    private data class ConstructorLambda(
        /**
         * Local function that constructs the target class.
         */
        val function: IrSimpleFunction,
        /**
         * Function reference expression for [function].
         */
        val reference: IrExpression,
    )
}

/**
 * Single constructor property of a value-class candidate.
 */
private val FiktionGeneratedMetadataCandidate.singleProperty: FiktionGeneratedMetadataPropertyCandidate
    get() = properties.single()

/**
 * Fiktion runtime package name.
 */
private const val FIKTION_PACKAGE = "dev.s7a.fiktion"

/**
 * Fully qualified name for top-level fake functions.
 */
private const val FIKTION_FAKE_FUNCTION = "$FIKTION_PACKAGE.fake"

/**
 * Returns a top-level callable id.
 */
private fun callableId(
    packageName: String,
    name: String,
): CallableId = CallableId(FqName(packageName), Name.identifier(name))

/**
 * Returns a top-level class id.
 */
private fun classId(fqName: String): ClassId = ClassId.topLevel(FqName(fqName))

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
 * Sets the dispatch receiver value.
 */
private fun IrMemberAccessExpression<*>.setDispatchReceiver(expression: IrExpression) {
    arguments[dispatchReceiverParameter] = expression
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
 * Function symbol targeted by this member access expression.
 */
private val IrMemberAccessExpression<*>.functionSymbol: IrFunctionSymbol
    get() = symbol as IrFunctionSymbol
