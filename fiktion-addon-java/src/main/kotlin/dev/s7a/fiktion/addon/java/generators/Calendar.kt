package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.util.Calendar
import java.util.GregorianCalendar

/**
 * Generates a Java calendar.
 */
public fun FakeContext.calendar(): Calendar = GregorianCalendar.from(zonedDateTime())
