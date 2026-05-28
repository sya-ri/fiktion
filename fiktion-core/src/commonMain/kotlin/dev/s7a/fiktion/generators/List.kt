package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a list using [element].
 */
public fun <T> FakeContext.list(
    size: Int = int(1..3),
    element: FakeContext.() -> T,
): List<T> =
    List(size) { index ->
        element(childContext(index = index))
    }

/**
 * Generates a mutable list using [element].
 */
public fun <T> FakeContext.mutableList(
    size: Int = int(1..3),
    element: FakeContext.() -> T,
): MutableList<T> = list(size = size, element = element).toMutableList()
