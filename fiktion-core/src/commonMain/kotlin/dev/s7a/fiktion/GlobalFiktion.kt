package dev.s7a.fiktion

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Holder for the process-wide global Fiktion configuration.
 */
@OptIn(ExperimentalAtomicApi::class)
internal object GlobalFiktion {
    /**
     * Atomic state for the current global configuration.
     */
    private val current = AtomicReference(GlobalFiktionState(version = 0, config = FiktionConfig()))

    /**
     * Current global configuration snapshot.
     */
    val config: FiktionConfig
        get() = current.load().config

    /**
     * Applies [configure] as an atomic global configuration update.
     */
    fun configure(configure: FiktionConfigureBuilder.() -> Unit): FiktionSnapshot {
        while (true) {
            val previous = current.load()
            val builder = DefaultFiktionBuilder(previous.config)
            builder.configure()
            val next = GlobalFiktionState(version = previous.version + 1, config = builder.build())
            if (current.compareAndSet(previous, next)) {
                return DefaultFiktionSnapshot(previous = previous, installed = next)
            }
        }
    }

    /**
     * Restores [previous] if [installed] is still current, unless [force] is enabled.
     */
    fun restore(
        previous: GlobalFiktionState,
        installed: GlobalFiktionState,
        force: Boolean,
    ): Boolean {
        if (!force) {
            return current.compareAndSet(
                expectedValue = installed,
                newValue = GlobalFiktionState(version = installed.version + 1, config = previous.config),
            )
        }

        while (true) {
            val active = current.load()
            val restored = GlobalFiktionState(version = active.version + 1, config = previous.config)
            if (current.compareAndSet(active, restored)) return true
        }
    }
}
