package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.boolean
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Generates a Java atomic boolean.
 */
public fun FakeContext.atomicBoolean(): AtomicBoolean = AtomicBoolean(boolean())
