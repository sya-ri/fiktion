package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.list
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Generates a Java copy-on-write array list using [element].
 */
public fun <T> FakeContext.copyOnWriteArrayList(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): CopyOnWriteArrayList<T> = CopyOnWriteArrayList(list(size = size, element = element))

/**
 * Generates a Java copy-on-write array list from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.copyOnWriteArrayList(): CopyOnWriteArrayList<Any?> =
    copyOnWriteArrayList(size = int(config(FiktionConfig.Collection.size))) {
        fakeElement(index)
    }
