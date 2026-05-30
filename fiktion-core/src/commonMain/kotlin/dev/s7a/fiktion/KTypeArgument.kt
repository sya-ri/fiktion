package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Returns the type argument at [index], if it is available and not projected as star.
 */
internal fun KType.typeArgument(index: Int): KType? = arguments.getOrNull(index)?.type
