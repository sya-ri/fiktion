package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.boolean
import java.util.Optional

/**
 * Generates a Java optional using [value].
 */
public fun <T : Any> FakeContext.optional(value: FakeContext.() -> T): Optional<T> =
    if (boolean()) {
        Optional.of(value())
    } else {
        Optional.empty()
    }

/**
 * Generates a Java optional from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.optional(): Optional<Any> =
    if (boolean()) {
        Optional.ofNullable(fake(0))
    } else {
        Optional.empty()
    }
