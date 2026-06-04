package dev.s7a.fiktion.generators

import kotlin.test.Test
import kotlin.test.assertEquals

class FiktionCharsetTest {
    @Test
    fun `constructor accepts iterable characters`() {
        val charset = FiktionCharset(listOf('a', 'b', 'c'))

        assertEquals("abc", charset.chars)
    }

    @Test
    fun `constructor accepts character ranges as iterable characters`() {
        val charset = FiktionCharset('a'..'c')

        assertEquals("abc", charset.chars)
    }

    @Test
    fun `plus combines character sets`() {
        val charset = FiktionCharset('a'..'c') + FiktionCharset('1'..'3')

        assertEquals("abc123", charset.chars)
    }

    @Test
    fun `plus accepts iterable characters`() {
        val charset = FiktionCharset('a'..'c') + listOf('1', '2', '3')

        assertEquals("abc123", charset.chars)
    }

    @Test
    fun `plus accepts character ranges as iterable characters`() {
        val charset = FiktionCharset('a'..'c') + ('1'..'3')

        assertEquals("abc123", charset.chars)
    }

    @Test
    fun `built in charsets are composed from character ranges`() {
        assertEquals("abcdefghijklmnopqrstuvwxyz", FiktionCharset.LowercaseAlpha.chars)
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", FiktionCharset.UppercaseAlpha.chars)
        assertEquals("0123456789", FiktionCharset.Numeric.chars)
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", FiktionCharset.UppercaseAlphaNumeric.chars)
        assertEquals(
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
            FiktionCharset.AlphaNumeric.chars,
        )
    }
}
