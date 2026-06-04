package dev.s7a.fiktion

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
