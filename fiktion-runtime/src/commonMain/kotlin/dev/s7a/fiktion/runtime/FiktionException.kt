package dev.s7a.fiktion.runtime

/**
 * Base exception for Fiktion runtime failures.
 */
public sealed class FiktionException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
