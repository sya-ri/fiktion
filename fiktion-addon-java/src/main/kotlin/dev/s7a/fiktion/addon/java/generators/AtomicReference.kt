package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import java.util.concurrent.atomic.AtomicReference

/**
 * Generates a Java atomic reference using [value].
 */
public fun <T> FakeContext.atomicReference(value: FakeContext.() -> T): AtomicReference<T> = AtomicReference(value())

/**
 * Generates a Java atomic reference from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.atomicReference(): AtomicReference<Any?> = AtomicReference(fake(0))
