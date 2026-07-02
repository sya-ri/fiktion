package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.list
import java.util.LinkedList

/**
 * Generates a Java linked list using [element].
 */
public fun <T> FakeContext.linkedList(
    size: Int = FiktionConfig.Collection.size(),
    element: FakeContext.() -> T,
): LinkedList<T> = LinkedList(list(size = size, element = element))

/**
 * Generates a Java linked list from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.linkedList(): LinkedList<Any?> =
    linkedList(size = FiktionConfig.Collection.size()) {
        fakeElement(index)
    }
