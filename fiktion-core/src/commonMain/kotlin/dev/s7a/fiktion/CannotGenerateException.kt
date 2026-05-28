package dev.s7a.fiktion

/**
 * Thrown when Fiktion cannot generate a value for the requested target.
 */
public class CannotGenerateException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
