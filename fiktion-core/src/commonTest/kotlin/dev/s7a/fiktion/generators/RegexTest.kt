package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegexTest {
    @Test
    fun `regex supports configured length and charset`() {
        val fiktion =
            Fiktion {
                type<Regex>() generatesBy {
                    regex(length = 8, charset = FiktionCharset.Numeric)
                }
            }

        val value = fiktion.fake<Regex>(seed = 1)

        assertEquals(8, value.pattern.length)
        assertTrue(value.pattern.all(Char::isDigit))
    }
}
