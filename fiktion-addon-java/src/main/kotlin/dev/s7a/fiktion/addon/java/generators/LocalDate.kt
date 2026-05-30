package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.addon.java.JavaFiktionConfig
import dev.s7a.fiktion.generators.long
import java.time.LocalDate

/**
 * Generates a Java local date from 1900-01-01 until 2101-01-01.
 */
public fun FakeContext.localDate(): LocalDate = LocalDate.ofEpochDay(long(config(JavaFiktionConfig.LocalDate.epochDays)))
