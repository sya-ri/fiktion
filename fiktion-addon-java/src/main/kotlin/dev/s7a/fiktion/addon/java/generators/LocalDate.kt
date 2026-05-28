package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.long
import java.time.LocalDate

/**
 * Generates a Java local date from 1900-01-01 until 2101-01-01.
 */
public fun FakeContext.localDate(): LocalDate = LocalDate.ofEpochDay(long(DEFAULT_MIN_EPOCH_DAY until DEFAULT_MAX_EPOCH_DAY_EXCLUSIVE))

private val DEFAULT_MIN_EPOCH_DAY: Long = LocalDate.of(1900, 1, 1).toEpochDay()
private val DEFAULT_MAX_EPOCH_DAY_EXCLUSIVE: Long = LocalDate.of(2101, 1, 1).toEpochDay()
