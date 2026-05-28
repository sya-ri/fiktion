package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.text.DateFormat

/**
 * Generates a Java date format.
 */
public fun FakeContext.dateFormat(): DateFormat =
    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale()).apply {
        timeZone = timeZone()
    }
