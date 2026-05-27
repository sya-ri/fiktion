package dev.s7a.fiktion.runtime

/**
 * Thrown when Fiktion cannot generate a value for the requested target.
 */
public class CannotGenerateException(
    message: String,
    cause: Throwable? = null,
) : FiktionException(message, cause)
