package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.list
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * Generates a Java concurrent linked deque using [element].
 */
public fun <T> FakeContext.concurrentLinkedDeque(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): ConcurrentLinkedDeque<T & Any> = ConcurrentLinkedDeque(list(size = size, element = element).filterNotNull())

/**
 * Generates a Java concurrent linked deque from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.concurrentLinkedDeque(): ConcurrentLinkedDeque<Any> =
    concurrentLinkedDeque(size = int(config(FiktionConfig.Collection.size))) {
        fakeElement(index)
    }
