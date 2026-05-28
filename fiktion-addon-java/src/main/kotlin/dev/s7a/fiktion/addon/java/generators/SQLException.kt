package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.string
import java.sql.SQLException

/**
 * Generates a Java SQL exception.
 */
public fun FakeContext.sqlException(message: String = string()): SQLException = SQLException(message)
