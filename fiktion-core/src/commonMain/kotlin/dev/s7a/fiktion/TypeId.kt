package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Suffix added by Kotlin/JVM when full reflection is unavailable.
 */
private const val REFLECTION_UNAVAILABLE_SUFFIX = " (Kotlin reflection is not available)"

/**
 * Returns the stable type id used for exact generated type matching.
 */
internal fun KType.typeId(): String = toString().removeSuffix(REFLECTION_UNAVAILABLE_SUFFIX)

/**
 * Returns the stable type id used when nullable and non-null values share structural metadata.
 */
internal fun KType.nonNullTypeId(): String = typeId().removeSuffix("?")
