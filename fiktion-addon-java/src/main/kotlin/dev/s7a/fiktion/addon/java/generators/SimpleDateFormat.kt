package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.text.SimpleDateFormat

/**
 * Generates a Java simple date format.
 */
public fun FakeContext.simpleDateFormat(): SimpleDateFormat =
    SimpleDateFormat(oneOf(SIMPLE_DATE_FORMAT_PATTERNS), locale()).apply {
        timeZone = timeZone()
    }

private val SIMPLE_DATE_FORMAT_PATTERNS: List<String> =
    listOf(
        "yyyy-MM-dd",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy/MM/dd HH:mm:ss",
        "EEE, dd MMM yyyy HH:mm:ss Z",
    )
