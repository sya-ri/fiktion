package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.text.NumberFormat

/**
 * Generates a Java number format.
 */
public fun FakeContext.numberFormat(): NumberFormat = NumberFormat.getNumberInstance(locale())
