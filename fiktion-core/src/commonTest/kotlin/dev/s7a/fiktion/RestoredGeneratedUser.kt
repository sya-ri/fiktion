package dev.s7a.fiktion

/**
 * Test model used to verify generated metadata survives snapshot restore.
 */
internal data class RestoredGeneratedUser(
    /**
     * User identifier.
     */
    val id: String,
)
