package dev.s7a.fiktion

/**
 * Restorable snapshot of the global Fiktion configuration.
 */
public sealed interface FiktionSnapshot {
    /**
     * Restores the configuration captured by this snapshot.
     *
     * When [force] is `false`, restoration only happens if the installed configuration is still current. When [force]
     * is `true`, restoration overwrites any newer global configuration. Returns `true` when the global configuration
     * was restored, or `false` when [force] is `false` and a newer configuration is already installed.
     */
    public fun restore(force: Boolean = false): Boolean
}
