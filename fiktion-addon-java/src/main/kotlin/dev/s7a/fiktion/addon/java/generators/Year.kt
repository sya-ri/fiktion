package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.time.Year

/**
 * Generates a Java year from 1900 through 2100.
 */
public fun FakeContext.year(): Year = Year.of(localDate().year)
