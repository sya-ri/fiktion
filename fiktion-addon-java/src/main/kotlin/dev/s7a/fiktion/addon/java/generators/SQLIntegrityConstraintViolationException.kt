package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.string
import java.sql.SQLIntegrityConstraintViolationException

/**
 * Generates a Java SQL integrity constraint violation exception.
 */
public fun FakeContext.sqlIntegrityConstraintViolationException(message: String = string()): SQLIntegrityConstraintViolationException =
    SQLIntegrityConstraintViolationException(message)
