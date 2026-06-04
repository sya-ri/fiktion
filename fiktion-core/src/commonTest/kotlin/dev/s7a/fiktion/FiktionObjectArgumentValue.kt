package dev.s7a.fiktion

/**
 * Returns the generated value or [defaultValue] when this argument asks to use a default.
 */
internal fun FiktionObjectArgument.valueOrDefault(defaultValue: Any?): Any? =
    when (this) {
        FiktionObjectDefault -> defaultValue
        is FiktionObjectValue -> value
    }
