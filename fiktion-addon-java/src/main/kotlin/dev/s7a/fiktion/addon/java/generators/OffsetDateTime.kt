package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.time.OffsetDateTime

/**
 * Generates a Java offset date-time.
 */
public fun FakeContext.offsetDateTime(): OffsetDateTime = zonedDateTime().toOffsetDateTime()
