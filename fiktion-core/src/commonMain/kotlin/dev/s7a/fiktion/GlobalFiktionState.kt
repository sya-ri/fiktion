package dev.s7a.fiktion

/**
 * Versioned global Fiktion state used for atomic snapshot restore checks.
 */
internal data class GlobalFiktionState(
    /**
     * Monotonic version assigned to each global state transition.
     */
    val version: Int,
    /**
     * Configuration active for this version.
     */
    val config: FiktionConfig,
)
