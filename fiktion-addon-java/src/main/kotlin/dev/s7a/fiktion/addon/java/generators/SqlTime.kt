package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.sql.Time

/**
 * Generates a SQL time.
 */
public fun FakeContext.sqlTime(): Time = Time.valueOf(localTime())
