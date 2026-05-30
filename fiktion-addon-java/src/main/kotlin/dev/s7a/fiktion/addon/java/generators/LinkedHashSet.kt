package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.set
import java.util.LinkedHashSet

/**
 * Generates a Java linked hash set using [element].
 */
public fun <T> FakeContext.linkedHashSet(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): LinkedHashSet<T> = LinkedHashSet(set(size = size, element = element))

/**
 * Generates a Java linked hash set from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.linkedHashSet(): LinkedHashSet<Any?> =
    linkedHashSet(size = int(config(FiktionConfig.Collection.size))) {
        fake(argumentIndex = 0, seedIndex = index)
    }
