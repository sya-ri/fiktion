package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.list
import java.util.ArrayList

/**
 * Generates a Java array list using [element].
 */
public fun <T> FakeContext.arrayList(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): ArrayList<T> = ArrayList(list(size = size, element = element))

/**
 * Generates a Java array list from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.arrayList(): ArrayList<Any?> =
    arrayList(size = int(config(FiktionConfig.Collection.size))) {
        fake(argumentIndex = 0, seedIndex = index)
    }
