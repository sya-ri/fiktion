@file:OptIn(dev.s7a.fiktion.runtime.ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.random.Random
import kotlin.reflect.KType

/**
 * Generates a fake [T] using [type], [baseConfig], optional [seed], and per-call [configure] rules.
 */
internal fun <T> generateFake(
    type: KType,
    baseConfig: FiktionConfig,
    seed: Long?,
    configure: FakeSpec<T>.() -> Unit,
): T {
    val spec = FakeSpec<T>()
    spec.configure()
    val rootSeed = spec.seed ?: seed ?: baseConfig.seed ?: Random.nextLong()
    val config =
        baseConfig.overlaidBy(
            other = FiktionConfig(rules = spec.rules),
            rulePrecedence = RulePrecedence.PER_CALL,
        )
    val request = GenerationRequest(type = type)
    val value = generateValue(request = request, config = config, seed = rootSeed, depth = 0)
    @Suppress("UNCHECKED_CAST")
    return value as T
}
