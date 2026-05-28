package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.util.concurrent.TimeUnit

/**
 * Generates a Java time unit.
 */
public fun FakeContext.timeUnit(): TimeUnit = oneOf(TimeUnit.entries)
