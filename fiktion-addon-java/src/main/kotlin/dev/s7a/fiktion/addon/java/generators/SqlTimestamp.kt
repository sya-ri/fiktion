package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.sql.Timestamp

/**
 * Generates a SQL timestamp.
 */
public fun FakeContext.sqlTimestamp(): Timestamp = Timestamp.from(instant())
