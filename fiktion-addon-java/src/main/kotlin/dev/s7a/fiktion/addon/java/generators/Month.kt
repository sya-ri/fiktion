package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.time.Month

/**
 * Generates a Java month.
 */
public fun FakeContext.month(): Month = oneOf(Month.entries)
