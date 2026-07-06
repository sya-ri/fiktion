package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.list
import java.util.concurrent.ArrayBlockingQueue

/**
 * Generates a Java array blocking queue using [element].
 */
public fun <T> FakeContext.arrayBlockingQueue(
    size: Int = FiktionConfig.Collection.size(),
    element: FakeContext.() -> T,
): ArrayBlockingQueue<T & Any> =
    ArrayBlockingQueue<T & Any>(size.coerceAtLeast(1)).apply {
        addAll(list(size = size, element = element).filterNotNull())
    }

/**
 * Generates a Java array blocking queue from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.arrayBlockingQueue(): ArrayBlockingQueue<Any> =
    arrayBlockingQueue(size = FiktionConfig.Collection.size()) {
        fakeElement(index)
    }
