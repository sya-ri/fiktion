package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * Fiktion runtime package name.
 */
internal const val FIKTION_PACKAGE: String = "dev.s7a.fiktion"

/**
 * Fully qualified name for top-level fake functions.
 */
internal const val FIKTION_FAKE_FUNCTION: String = "$FIKTION_PACKAGE.fake"

/**
 * Fully qualified name for factory-construction declarations.
 */
internal const val FIKTION_CONSTRUCTS_BY_FUNCTION: String = "$FIKTION_PACKAGE.constructsBy"

/**
 * Generated top-level registrar function name.
 */
internal const val FIKTION_GENERATED_REGISTRAR_NAME: String = $$"$fiktionRegisterGeneratedMetadata"

/**
 * Generated top-level registrar initialized field name.
 */
internal const val FIKTION_GENERATED_REGISTRAR_FIELD_NAME: String = $$"$fiktionGeneratedMetadataRegistered"

/**
 * Returns a top-level callable id.
 */
internal fun callableId(
    packageName: String,
    name: String,
): CallableId = CallableId(FqName(packageName), Name.identifier(name))

/**
 * Returns a top-level class id.
 */
internal fun classId(fqName: String): ClassId = ClassId.topLevel(FqName(fqName))
