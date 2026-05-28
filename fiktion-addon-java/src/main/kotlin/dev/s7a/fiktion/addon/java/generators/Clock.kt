package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.time.Clock

/**
 * Generates a Java clock fixed to a generated instant and zone.
 */
public fun FakeContext.clock(): Clock = Clock.fixed(instant(), zoneId())
