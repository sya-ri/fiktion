package dev.s7a.fiktion.addon.arrow.generators

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.boolean

/**
 * Generates an Arrow option using [value].
 */
public fun <T> FakeContext.option(value: FakeContext.() -> T): Option<T> =
    if (boolean()) {
        Some(value())
    } else {
        None
    }

/**
 * Generates an Arrow option from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.option(): Option<Any?> =
    if (boolean()) {
        Some(fake(0))
    } else {
        None
    }
