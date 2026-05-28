package dev.s7a.fiktion

/**
 * Thrown when Fiktion configuration or DSL input is invalid.
 */
public class FiktionConfigurationException(
    message: String,
    cause: Throwable? = null,
) : IllegalArgumentException(message, cause)

/**
 * Throws [FiktionConfigurationException] unless [value] is true.
 */
internal inline fun requireFiktionConfiguration(
    value: Boolean,
    lazyMessage: () -> String,
) {
    if (!value) {
        throw FiktionConfigurationException(lazyMessage())
    }
}
