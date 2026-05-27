package dev.s7a.fiktion.runtime

/**
 * Thrown when a configured rule is invalid or cannot be applied.
 */
public class InvalidRuleException(
    message: String,
    cause: Throwable? = null,
) : FiktionException(message, cause)
