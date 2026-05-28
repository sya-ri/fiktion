package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.util.Currency

/**
 * Generates a Java currency from the available JDK currencies.
 */
public fun FakeContext.currency(): Currency = oneOf(JAVA_CURRENCIES)

private val JAVA_CURRENCIES: List<Currency> =
    Currency.getAvailableCurrencies().sortedBy { currency ->
        currency.currencyCode
    }
