package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.list
import java.util.ArrayDeque
import java.util.Queue

/**
 * Generates a Java queue using [element].
 */
public fun <T> FakeContext.queue(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): Queue<T> = ArrayDeque(list(size = size, element = element).filterNotNull())

/**
 * Generates a Java queue from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.queue(): Queue<Any?> = arrayDeque()
