package dev.s7a.fiktion

/**
 * Test sealed subtype containing text.
 */
data class TextMessage(
    /**
     * Generated text.
     */
    val text: String,
) : Message
