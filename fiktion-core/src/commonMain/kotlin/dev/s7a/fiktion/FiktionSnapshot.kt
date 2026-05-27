package dev.s7a.fiktion

/**
 * Restorable snapshot of the global Fiktion configuration.
 */
public interface FiktionSnapshot {
    /**
     * Restores the configuration captured by this snapshot.
     */
    public fun restore()
}
