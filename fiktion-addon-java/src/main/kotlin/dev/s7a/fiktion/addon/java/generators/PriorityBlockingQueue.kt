package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.list
import java.util.concurrent.PriorityBlockingQueue

/**
 * Generates a Java priority blocking queue using [element].
 */
public fun <T> FakeContext.priorityBlockingQueue(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): PriorityBlockingQueue<T & Any> =
    PriorityBlockingQueue<T & Any>(size.coerceAtLeast(1), compareBy { value -> value.toString() }).apply {
        addAll(list(size = size, element = element).filterNotNull())
    }

/**
 * Generates a Java priority blocking queue from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.priorityBlockingQueue(): PriorityBlockingQueue<Any> =
    priorityBlockingQueue(size = int(config(FiktionConfig.Collection.size))) {
        fake(argumentIndex = 0, seedIndex = index)
    }
