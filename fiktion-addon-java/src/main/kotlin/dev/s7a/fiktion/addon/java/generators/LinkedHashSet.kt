package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.set
import java.util.LinkedHashSet

/**
 * Generates a Java linked hash set using [element].
 */
public fun <T> FakeContext.linkedHashSet(
    size: Int = int(1..3),
    element: FakeContext.() -> T,
): LinkedHashSet<T> = LinkedHashSet(set(size = size, element = element))

/**
 * Generates a Java linked hash set from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.linkedHashSet(): LinkedHashSet<Any?> =
    linkedHashSet(size = int(1..3)) {
        fake(argumentIndex = 0, seedIndex = index)
    }
