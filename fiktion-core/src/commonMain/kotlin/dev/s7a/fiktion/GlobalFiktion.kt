@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Holder for the process-wide global Fiktion configuration.
 */
@OptIn(ExperimentalAtomicApi::class)
internal object GlobalFiktion {
    /**
     * Add-ons registered by code that runs on every supported platform.
     */
    private val automaticAddons = AtomicReference<List<FiktionAddon>>(emptyList())

    /**
     * Atomic state for the current user-controlled global configuration.
     */
    private val current = AtomicReference(GlobalFiktionState(version = 0, config = DefaultFiktionBuilder().build()))

    /**
     * Compiler-generated metadata that stays outside snapshot restore.
     */
    private val generatedMetadata = AtomicReference<Map<String, FiktionTypeMetadata<*>>>(emptyMap())

    /**
     * Current effective global configuration snapshot.
     */
    val config: FiktionConfigState
        get() =
            FiktionConfigState(metadata = generatedMetadata.load()).overlaidBy(
                other = current.load().config,
                rulePrecedence = RulePrecedence.GLOBAL,
            )

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
     * Registers compiler-generated [metadata] without making it part of restorable user configuration.
     */
    fun registerGenerated(metadata: FiktionTypeMetadata<*>) {
        while (true) {
            val previous = generatedMetadata.load()
            val next = previous + (metadata.type.typeId() to metadata)
            if (generatedMetadata.compareAndSet(previous, next)) return
        }
    }

    /**
     * Registers an add-on so compiler-generated calls can install it automatically.
     */
    fun registerAutomaticAddon(addon: FiktionAddon) {
        while (true) {
            val previous = automaticAddons.load()
            val next = previous.filterNot { installed -> installed.id == addon.id } + addon
            if (automaticAddons.compareAndSet(previous, next)) return
        }
    }

    /**
     * Returns add-ons discoverable on the current platform.
     */
    fun automaticAddons(): List<FiktionAddon> = automaticAddons.load()

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
