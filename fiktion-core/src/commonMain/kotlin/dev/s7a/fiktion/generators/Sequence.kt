package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext

/**
 * Generates a finite sequence using [element].
 */
public fun <T> FakeContext.sequence(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): Sequence<T> = list(size = size, element = element).asSequence()

/**
 * Generates a finite sequence from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.sequence(): Sequence<Any?> = list().asSequence()
