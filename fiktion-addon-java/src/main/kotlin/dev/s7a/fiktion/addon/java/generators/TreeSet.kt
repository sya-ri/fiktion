package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.list
import java.util.TreeSet

/**
 * Generates a Java tree set using [element].
 */
public fun <T> FakeContext.treeSet(
    size: Int = int(1..3),
    element: FakeContext.() -> T,
): TreeSet<T & Any> =
    TreeSet<T & Any>(compareBy { value -> value.toString() }).apply {
        addAll(list(size = size, element = element).filterNotNull())
    }

/**
 * Generates a Java tree set from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.treeSet(): TreeSet<Any> =
    treeSet(size = int(1..3)) {
        fake(argumentIndex = 0, seedIndex = index)
    }
