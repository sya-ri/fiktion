package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.list
import java.util.concurrent.ConcurrentSkipListSet

/**
 * Generates a Java concurrent skip list set using [element].
 */
public fun <T : Any> FakeContext.concurrentSkipListSet(
    size: Int = FiktionConfig.Collection.size(),
    element: FakeContext.() -> T,
): ConcurrentSkipListSet<T> =
    ConcurrentSkipListSet<T>(compareBy { value -> value.toString() }).apply {
        addAll(list(size = size, element = element))
    }

/**
 * Generates a Java concurrent skip list set from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.concurrentSkipListSet(): ConcurrentSkipListSet<Any> =
    ConcurrentSkipListSet<Any>(compareBy { value -> value.toString() }).apply {
        repeat(FiktionConfig.Collection.size()) { index ->
            fakeElement(index)?.let(::add)
        }
    }
