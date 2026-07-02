package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.requireFiktionConfiguration

/**
 * Chooses one value from [values].
 */
public fun <T> FakeContext.oneOf(vararg values: T): T = oneOf(values.toList())

/**
 * Chooses one value from [values].
 */
public fun <T> FakeContext.oneOf(values: List<T>): T {
    requireFiktionConfiguration(values.isNotEmpty()) { "values must not be empty." }
    return values[random.nextInt(values.size)]
}

/**
 * Returns [this] without [value].
 *
 * Chained exclusions are cumulative.
 */
public infix fun <T> Iterable<T>.excluding(value: T): List<T> = filterNot { candidate -> candidate == value }

/**
 * Returns [this] without [values].
 *
 * Chained exclusions are cumulative.
 */
public infix fun <T> Iterable<T>.excluding(values: Iterable<T>): List<T> {
    val excluded = values.toList()
    return filterNot { candidate -> excluded.any { value -> value == candidate } }
}

/**
 * Returns [this] without candidates matching [predicate].
 *
 * Chained exclusions are cumulative.
 */
public infix fun <T> Iterable<T>.excluding(predicate: (T) -> Boolean): List<T> = filterNot(predicate)
