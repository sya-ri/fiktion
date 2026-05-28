package dev.s7a.fiktion

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Add-ons registered by code that runs on every supported platform.
 */
@OptIn(ExperimentalAtomicApi::class)
private val registeredAutomaticAddons = AtomicReference<List<FiktionAddon>>(emptyList())

@OptIn(ExperimentalAtomicApi::class)
internal fun registerAutomaticAddon(addon: FiktionAddon) {
    while (true) {
        val previous = registeredAutomaticAddons.load()
        val next = previous.filterNot { installed -> installed.id == addon.id } + addon
        if (registeredAutomaticAddons.compareAndSet(previous, next)) return
    }
}

/**
 * Returns add-ons discoverable on the current platform.
 */
@OptIn(ExperimentalAtomicApi::class)
internal fun automaticAddons(): List<FiktionAddon> = registeredAutomaticAddons.load()
