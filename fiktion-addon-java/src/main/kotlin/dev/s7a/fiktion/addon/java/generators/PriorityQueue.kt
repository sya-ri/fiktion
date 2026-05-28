package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.list
import java.util.PriorityQueue

/**
 * Generates a Java priority queue using [element].
 */
public fun <T> FakeContext.priorityQueue(
    size: Int = int(1..3),
    element: FakeContext.() -> T,
): PriorityQueue<T & Any> =
    PriorityQueue<T & Any>(compareBy { value -> value.toString() }).apply {
        addAll(list(size = size, element = element).filterNotNull())
    }

/**
 * Generates a Java priority queue from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.priorityQueue(): PriorityQueue<Any> =
    priorityQueue(size = int(1..3)) {
        fake(argumentIndex = 0, seedIndex = index)
    }
