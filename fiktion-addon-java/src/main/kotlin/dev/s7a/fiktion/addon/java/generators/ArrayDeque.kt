package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.list
import java.util.ArrayDeque

/**
 * Generates a Java array deque using [element].
 */
public fun <T> FakeContext.arrayDeque(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): ArrayDeque<T> = ArrayDeque(list(size = size, element = element).filterNotNull())

/**
 * Generates a Java array deque from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.arrayDeque(): ArrayDeque<Any?> =
    arrayDeque(size = int(config(FiktionConfig.Collection.size))) {
        fakeElement(index)
    }
