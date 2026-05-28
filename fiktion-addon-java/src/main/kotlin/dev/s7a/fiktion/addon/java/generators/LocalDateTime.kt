package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.time.LocalDateTime

/**
 * Generates a Java local date-time.
 */
public fun FakeContext.localDateTime(): LocalDateTime = LocalDateTime.of(localDate(), localTime())
