package dev.s7a.fiktion

/**
 * Test model used for map property generation.
 */
internal data class Project(
    /**
     * Project labels keyed by label id.
     */
    val labels: Map<String, String>,
)
