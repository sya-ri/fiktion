package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.int
import java.util.concurrent.atomic.AtomicInteger

/**
 * Generates a Java atomic integer.
 */
public fun FakeContext.atomicInteger(): AtomicInteger = AtomicInteger(int())
