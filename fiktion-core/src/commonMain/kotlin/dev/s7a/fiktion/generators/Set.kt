package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a set using [element].
 */
public fun <T> FakeContext.set(
    size: Int = int(1..3),
    element: FakeContext.() -> T,
): Set<T> = list(size = size, element = element).toSet()

/**
 * Generates a mutable set using [element].
 */
public fun <T> FakeContext.mutableSet(
    size: Int = int(1..3),
    element: FakeContext.() -> T,
): MutableSet<T> = set(size = size, element = element).toMutableSet()
