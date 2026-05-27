package dev.s7a.fiktion.runtime

/**
 * Thrown when generated metadata is not compatible with the runtime.
 */
public class IncompatibleMetadataException(
    message: String,
    cause: Throwable? = null,
) : FiktionException(message, cause)
