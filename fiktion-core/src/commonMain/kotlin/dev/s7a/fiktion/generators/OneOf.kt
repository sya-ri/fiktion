package dev.s7a.fiktion.generators

import dev.s7a.fiktion.runtime.FakeContext

/**
 * Chooses one value from [values].
 */
public fun <T> FakeContext.oneOf(values: List<T>): T {
    require(values.isNotEmpty()) { "values must not be empty." }
    return values[random.nextInt(values.size)]
}
