package dev.s7a.fiktion

import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Generates fake data for [T] using the global Fiktion configuration.
 */
@Suppress("DEPRECATION_ERROR")
public inline fun <reified T> fake(
    seed: Long? = null,
    noinline configure: FakeSpec<T>.() -> Unit = {},
): T = fake(type = typeOf<T>(), seed = seed, configure = configure)

/**
 * Generates fake data for [T] using the global Fiktion configuration and explicit [type].
 *
 * This low-level overload is intended for callers that already carry a [KType]. The caller must keep [type] and [T]
 * consistent.
 */
@Deprecated("Use the reified fake<T>() overload.", level = DeprecationLevel.ERROR)
public fun <T> fake(
    type: KType,
    seed: Long? = null,
    configure: FakeSpec<T>.() -> Unit = {},
): T =
    generateFake(
        type = type,
        baseConfig = GlobalFiktion.config,
        seed = seed,
        configure = configure,
    )

/**
 * Generates fake data for [T] using this Fiktion instance.
 */
@Suppress("DEPRECATION_ERROR")
public inline fun <reified T> Fiktion.fake(
    seed: Long? = null,
    noinline configure: FakeSpec<T>.() -> Unit = {},
): T = fake(type = typeOf<T>(), seed = seed, configure = configure)

/**
 * Generates fake data for [T] using this Fiktion instance and explicit [type].
 *
 * This low-level overload is intended for callers that already carry a [KType]. The caller must keep [type] and [T]
 * consistent.
 */
@Deprecated("Use the reified Fiktion.fake<T>() overload.", level = DeprecationLevel.ERROR)
public fun <T> Fiktion.fake(
    type: KType,
    seed: Long? = null,
    configure: FakeSpec<T>.() -> Unit = {},
): T =
    generateFake(
        type = type,
        baseConfig = configOf(this),
        seed = seed,
        configure = configure,
    )
