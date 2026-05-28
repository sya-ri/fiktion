package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.long
import java.util.concurrent.atomic.AtomicLong

/**
 * Generates a Java atomic long.
 */
public fun FakeContext.atomicLong(): AtomicLong = AtomicLong(long())
