@file:Suppress("DEPRECATION", "DEPRECATION_ERROR")
@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
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
     * `FiktionTypeMetadata` class.
     */
    private val typeMetadataClass: IrClassSymbol =
        requireNotNull(pluginContext.referenceClass(classId("$FIKTION_PACKAGE.FiktionTypeMetadata")))

    /**
     * `FiktionAddon` class.
     */
    private val addonClass: IrClassSymbol =
        requireNotNull(pluginContext.referenceClass(classId("$FIKTION_PACKAGE.FiktionAddon")))

    /**
     * `Fiktion.register` companion function for generated metadata.
     */
    val registerMetadata: IrSimpleFunctionSymbol =
        fiktionCompanionClass.owner.declarations
            .filterIsInstance<IrSimpleFunction>()
            .single { function ->
                function.name == Name.identifier("register") &&
                    function.parameters.any { parameter ->
                        parameter.kind == IrParameterKind.Regular &&
                            parameter.type.classOrNull == typeMetadataClass
                    }
            }.symbol

    /**
     * `Fiktion.register` companion function for automatic addons.
     */
    val registerAddon: IrSimpleFunctionSymbol =
        fiktionCompanionClass.owner.declarations
            .filterIsInstance<IrSimpleFunction>()
            .single { function ->
                function.name == Name.identifier("register") &&
                    function.parameters.any { parameter ->
                        parameter.kind == IrParameterKind.Regular &&
                            parameter.type.classOrNull == addonClass
                    }
            }.symbol

    /**
     * `List.get` member function.
     */
    val listGet: IrSimpleFunctionSymbol =
        pluginContext.irBuiltIns.listClass.owner.declarations
            .filterIsInstance<IrSimpleFunction>()
            .single { function ->
                function.name == Name.identifier("get") &&
                    function.parameters.any { parameter -> parameter.kind == IrParameterKind.DispatchReceiver } &&
                    function.parameters.count { parameter -> parameter.kind == IrParameterKind.Regular } == 1
            }.symbol

    /**
     * `FiktionObjectDefault` object class.
     */
    val objectDefaultClass: IrClassSymbol =
        requireNotNull(
            pluginContext.referenceClass(classId("$FIKTION_PACKAGE.FiktionObjectDefault")),
        )

    /**
     * `FiktionObjectDefault` object type.
     */
    val objectDefaultType: IrType = objectDefaultClass.owner.defaultType

    /**
     * `FiktionObjectValue` type.
     */
    private val objectValueClass: IrClassSymbol =
        requireNotNull(
            pluginContext.referenceClass(classId("$FIKTION_PACKAGE.FiktionObjectValue")),
        )

    /**
     * `FiktionObjectValue` type.
     */
    val objectValueType: IrType = objectValueClass.owner.defaultType

    /**
     * `FiktionObjectValue.value` property getter.
     */
    val objectValueGetter: IrSimpleFunctionSymbol =
        requireNotNull(
            objectValueClass.owner.declarations
                .filterIsInstance<IrProperty>()
                .single { property -> property.name == Name.identifier("value") }
                .getter,
        ).symbol

    /**
     * `typeOf` top-level function.
     */
    val typeOf: IrSimpleFunctionSymbol = pluginContext.referenceFunctions(callableId("kotlin.reflect", "typeOf")).single()

    /**
     * `Collection<T>.toTypedArray` extension function.
     */
    val toTypedArray: IrSimpleFunctionSymbol =
        pluginContext
            .referenceFunctions(callableId("kotlin.collections", "toTypedArray"))
            .single { function ->
                function.owner.parameters
                    .singleOrNull { parameter -> parameter.kind == IrParameterKind.ExtensionReceiver }
                    ?.type
                    ?.classOrNull == pluginContext.irBuiltIns.collectionClass &&
                    function.owner.returnType.classOrNull == pluginContext.irBuiltIns.arrayClass
            }

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
