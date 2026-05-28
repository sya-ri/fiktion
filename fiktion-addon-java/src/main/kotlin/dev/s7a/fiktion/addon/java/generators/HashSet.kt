package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.set
import java.util.HashSet

/**
 * Generates a Java hash set using [element].
 */
public fun <T> FakeContext.hashSet(
    size: Int = int(1..3),
    element: FakeContext.() -> T,
): HashSet<T> = HashSet(set(size = size, element = element))

/**
 * Generates a Java hash set from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.hashSet(): HashSet<Any?> =
    hashSet(size = int(1..3)) {
        fake(argumentIndex = 0, seedIndex = index)
    }
