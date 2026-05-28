package dev.s7a.fiktion

/**
 * Test sealed subtype containing a URL.
 */
data class ImageMessage(
    /**
     * Generated image URL.
     */
    val url: String,
) : Message
