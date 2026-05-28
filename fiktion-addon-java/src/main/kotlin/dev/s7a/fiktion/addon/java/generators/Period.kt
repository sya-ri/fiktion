package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.time.Period

/**
 * Generates a Java period between two generated local dates.
 */
public fun FakeContext.period(): Period = Period.between(localDate(), localDate())
