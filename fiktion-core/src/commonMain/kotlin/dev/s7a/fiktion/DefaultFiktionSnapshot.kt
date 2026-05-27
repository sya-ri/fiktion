package dev.s7a.fiktion

/**
 * Snapshot implementation that restores the previous global configuration when it is still current.
 */
internal class DefaultFiktionSnapshot(
    /**
     * Global state before this snapshot was installed.
     */
    private val previous: GlobalFiktionState,
    /**
     * Global state installed by the matching configure call.
     */
    private val installed: GlobalFiktionState,
) : FiktionSnapshot {
    /**
     * Restores [previous] when [force] is true or [installed] is still the active global configuration.
     */
    override fun restore(force: Boolean): Boolean = GlobalFiktion.restore(previous = previous, installed = installed, force = force)
}
