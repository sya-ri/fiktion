package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.FiktionConfigurationException
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CharRangeTest {
    @Test
    fun `charRange generates non-empty ranges`() {
        val fiktion =
            Fiktion {
                type<CharRange>() generatesBy { charRange() }
            }

        assertFalse(fiktion.fake<CharRange>(seed = 1).isEmpty())
    }

    @Test
    fun `charRange uses alpha numeric characters by default`() {
        val ranges =
            List(100) { index ->
                fake<CharRange>(seed = index.toLong())
            }

        assertTrue(ranges.all { range -> range.all(Char::isLetterOrDigit) })
        assertTrue(ranges.any { range -> range.first.isUpperCase() || range.last.isUpperCase() })
        assertTrue(ranges.any { range -> range.first.isDigit() || range.last.isDigit() })
    }

    @Test
    fun `charRange supports configured charsets`() {
        val fiktion =
            Fiktion {
                type<CharRange>() generatesBy {
                    charRange(charset = FiktionCharset.Numeric)
                }
            }

        val value = fiktion.fake<CharRange>(seed = 1)

        assertTrue(value.first.isDigit())
        assertTrue(value.last.isDigit())
    }

    @Test
    fun `charRange rejects non-contiguous charsets`() {
        val fiktion =
            Fiktion {
                type<CharRange>() generatesBy {
                    charRange(charset = FiktionCharset("az09"))
                }
            }

        assertFailsWith<FiktionConfigurationException> {
            fiktion.fake<CharRange>(seed = 1)
        }
    }
}
