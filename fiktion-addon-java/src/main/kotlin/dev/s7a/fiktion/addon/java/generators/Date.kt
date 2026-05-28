package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.util.Date

/**
 * Generates a Java date.
 */
public fun FakeContext.date(): Date = Date.from(instant())
