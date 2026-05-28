package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class CharTest {
    @Test
    fun `char supports configured ranges`() {
        val fiktion =
            Fiktion {
                type<Char>() generatesBy { char('a', 'f') }
            }

        assertTrue(fiktion.fake<Char>(seed = 1) in 'a'..'f')
    }

    @Test
    fun `char supports configured charsets`() {
        val fiktion =
            Fiktion {
                type<Char>() generatesBy { char(charset = FiktionCharset.Numeric) }
            }

        assertTrue(fiktion.fake<Char>(seed = 1).isDigit())
    }
}
