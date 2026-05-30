package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.list
import java.util.concurrent.LinkedBlockingQueue

/**
 * Generates a Java linked blocking queue using [element].
 */
public fun <T> FakeContext.linkedBlockingQueue(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): LinkedBlockingQueue<T & Any> = LinkedBlockingQueue(list(size = size, element = element).filterNotNull())

/**
 * Generates a Java linked blocking queue from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.linkedBlockingQueue(): LinkedBlockingQueue<Any> =
    linkedBlockingQueue(size = int(config(FiktionConfig.Collection.size))) {
        fake(argumentIndex = 0, seedIndex = index)
    }
