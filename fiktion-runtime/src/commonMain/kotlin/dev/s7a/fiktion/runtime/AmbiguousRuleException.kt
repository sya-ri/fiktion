package dev.s7a.fiktion.runtime

/**
 * Thrown when multiple rules match and Fiktion cannot choose a single rule.
 */
public class AmbiguousRuleException(
    message: String,
    cause: Throwable? = null,
) : FiktionException(message, cause)
