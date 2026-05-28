package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.sql.Date

/**
 * Generates a SQL date.
 */
public fun FakeContext.sqlDate(): Date = Date.valueOf(localDate())
