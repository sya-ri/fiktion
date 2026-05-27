package dev.s7a.fiktion

/**
 * Generates fake data for [T] using the global Fiktion configuration.
 */
public inline fun <reified T> fake(
    seed: Long? = null,
    noinline configure: FakeSpec<T>.() -> Unit = {},
): T = throw NotImplementedError("Fiktion fake generation is not implemented yet.")

/**
 * Generates fake data for [T] using this Fiktion instance.
 */
public inline fun <reified T> Fiktion.fake(
    seed: Long? = null,
    noinline configure: FakeSpec<T>.() -> Unit = {},
): T = throw NotImplementedError("Fiktion fake generation is not implemented yet.")
