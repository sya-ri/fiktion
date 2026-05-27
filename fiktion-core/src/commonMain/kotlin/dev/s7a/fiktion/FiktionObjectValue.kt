package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.ExperimentalFiktionApi

/**
 * Constructor argument containing a generated [value].
 */
@ExperimentalFiktionApi
public data class FiktionObjectValue(
    /**
     * Generated constructor argument value.
     */
    public val value: Any?,
) : FiktionObjectArgument
