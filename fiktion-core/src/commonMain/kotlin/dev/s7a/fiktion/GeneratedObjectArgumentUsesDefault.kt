package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.ExperimentalFiktionApi

/**
 * Returns whether the generated constructor argument at [index] asks Kotlin to use the property's default value.
 *
 * This entry point is intended for compiler-generated metadata constructor functions.
 */
@ExperimentalFiktionApi
public fun generatedObjectArgumentUsesDefault(
    arguments: List<FiktionObjectArgument>,
    index: Int,
): Boolean = arguments[index] == FiktionObjectDefault
