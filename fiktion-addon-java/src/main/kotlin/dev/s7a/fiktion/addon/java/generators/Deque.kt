package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.list
import java.util.ArrayDeque
import java.util.Deque

/**
 * Generates a Java deque using [element].
 */
public fun <T> FakeContext.deque(
    size: Int = int(1..3),
    element: FakeContext.() -> T,
): Deque<T> = ArrayDeque(list(size = size, element = element).filterNotNull())

/**
 * Generates a Java deque from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.deque(): Deque<Any?> = arrayDeque()
