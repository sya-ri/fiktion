package dev.s7a.fiktion

import dev.s7a.fiktion.ExperimentalFiktionApi

/**
 * Returns the generated constructor argument value at [index].
 *
 * This entry point is intended for compiler-generated metadata constructor functions.
 */
@ExperimentalFiktionApi
public fun generatedObjectArgumentValue(
    arguments: List<FiktionObjectArgument>,
    index: Int,
): Any? = (arguments[index] as FiktionObjectValue).value
