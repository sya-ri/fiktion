@file:Suppress("DEPRECATION", "DEPRECATION_ERROR")
@file:OptIn(org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI::class)

package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * Runtime symbol references used by generated metadata registration.
 */
internal class FiktionRuntimeSymbols(
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
     * `generatedArray` top-level function.
     */
    val generatedArray: IrSimpleFunctionSymbol =
        pluginContext.referenceFunctions(callableId(FIKTION_PACKAGE, "generatedArray")).single()

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
     * `FiktionArrayMetadata` constructor.
     */
    val arrayMetadataConstructor: IrConstructorSymbol =
        pluginContext.referenceConstructors(classId("$FIKTION_PACKAGE.FiktionArrayMetadata")).single()

    /**
     * `FiktionValueMetadata` constructor.
     */
    val valueMetadataConstructor: IrConstructorSymbol =
        pluginContext.referenceConstructors(classId("$FIKTION_PACKAGE.FiktionValueMetadata")).single()

    /**
     * `FiktionEnumMetadata` constructor.
     */
    val enumMetadataConstructor: IrConstructorSymbol =
        pluginContext.referenceConstructors(classId("$FIKTION_PACKAGE.FiktionEnumMetadata")).single()

    /**
     * `FiktionSealedMetadata` constructor.
     */
    val sealedMetadataConstructor: IrConstructorSymbol =
        pluginContext.referenceConstructors(classId("$FIKTION_PACKAGE.FiktionSealedMetadata")).single()

    /**
     * `KType` type.
     */
    val kType: IrType =
        requireNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName("kotlin.reflect.KType")))).owner.defaultType

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

    /**
     * `List<Any?>` type used by array constructor lambdas.
     */
    val anyListType: IrType = pluginContext.irBuiltIns.listClass.typeWith(pluginContext.irBuiltIns.anyNType)
}
