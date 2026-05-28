package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.long
import java.math.BigDecimal

/**
 * Generates a Java big decimal.
 */
public fun FakeContext.bigDecimal(): BigDecimal = BigDecimal.valueOf(long(), int(0..4))
