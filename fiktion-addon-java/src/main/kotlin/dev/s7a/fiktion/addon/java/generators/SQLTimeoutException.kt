package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.string
import java.sql.SQLTimeoutException

/**
 * Generates a Java SQL timeout exception.
 */
public fun FakeContext.sqlTimeoutException(message: String = string()): SQLTimeoutException = SQLTimeoutException(message)
