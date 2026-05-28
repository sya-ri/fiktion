package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.time.format.DateTimeFormatter

/**
 * Generates a Java date-time formatter.
 */
public fun FakeContext.dateTimeFormatter(): DateTimeFormatter =
    oneOf(
        DateTimeFormatter.BASIC_ISO_DATE,
        DateTimeFormatter.ISO_DATE,
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ISO_INSTANT,
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_LOCAL_TIME,
        DateTimeFormatter.ISO_OFFSET_DATE,
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ISO_OFFSET_TIME,
        DateTimeFormatter.ISO_ORDINAL_DATE,
        DateTimeFormatter.ISO_TIME,
        DateTimeFormatter.ISO_WEEK_DATE,
        DateTimeFormatter.ISO_ZONED_DATE_TIME,
        DateTimeFormatter.RFC_1123_DATE_TIME,
    )
