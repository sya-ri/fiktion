package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.list
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Generates a Java concurrent linked queue using [element].
 */
public fun <T> FakeContext.concurrentLinkedQueue(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): ConcurrentLinkedQueue<T & Any> = ConcurrentLinkedQueue(list(size = size, element = element).filterNotNull())

/**
 * Generates a Java concurrent linked queue from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.concurrentLinkedQueue(): ConcurrentLinkedQueue<Any> =
    concurrentLinkedQueue(size = int(config(FiktionConfig.Collection.size))) {
        fakeElement(index)
    }
