package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.util.Locale

/**
 * Generates a Java locale from the available JDK locales.
 */
public fun FakeContext.locale(): Locale = oneOf(JAVA_LOCALES)

private val JAVA_LOCALES: List<Locale> =
    Locale
        .getAvailableLocales()
        .filter { locale -> locale.toLanguageTag().isNotBlank() }
        .sortedBy { locale -> locale.toLanguageTag() }
