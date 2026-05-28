package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a finite sequence using [element].
 */
public fun <T> FakeContext.sequence(
    size: Int = int(1..3),
    element: FakeContext.() -> T,
): Sequence<T> = list(size = size, element = element).asSequence()
