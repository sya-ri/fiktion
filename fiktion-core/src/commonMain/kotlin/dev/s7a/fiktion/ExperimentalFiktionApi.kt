package dev.s7a.fiktion

/**
 * Marks APIs that are expected to evolve before Fiktion reaches 1.0.
 */
@RequiresOptIn(
    message = "This Fiktion API is experimental and may change before 1.0.",
    level = RequiresOptIn.Level.WARNING,
)
public annotation class ExperimentalFiktionApi
