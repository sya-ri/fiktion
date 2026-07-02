@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.random.Random
import kotlin.reflect.KType

/**
 * Generates a fake [T] using [type], [baseConfig], optional [seed], and per-call [configure] rules.
 */
internal fun <T> generateFake(
    type: KType,
    baseConfig: FiktionConfigState,
    seed: Long?,
    configure: FakeSpec<T>.() -> Unit,
): T {
    val spec = FakeSpec<T>()
    spec.rootType = type
    spec.configure()
    val rootSeed = spec.seed ?: seed ?: baseConfig.seed ?: Random.nextLong()
    val config =
        baseConfig
            .withAutomaticAddons()
            .overlaidBy(
                other = FiktionConfigState(rules = spec.rules, configs = spec.configs),
                rulePrecedence = RulePrecedence.PER_CALL,
            )
    val request = GenerationRequest(type = type)
    val value =
        generateValue(
            request = request,
            config = config,
            seed = rootSeed,
            depth = 0,
            dependencyValues = null,
        )
    @Suppress("UNCHECKED_CAST")
    return value as T
}

/**
 * Returns [this] with the current automatic add-ons installed.
 */
private fun FiktionConfigState.withAutomaticAddons(): FiktionConfigState {
    val builder = DefaultFiktionBuilder(config = this, installAutomaticAddons = false)
    builder.installAutomaticAddons()
    return builder.build()
}
