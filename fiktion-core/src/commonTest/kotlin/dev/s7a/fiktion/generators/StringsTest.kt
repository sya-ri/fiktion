package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.type
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringsTest {
    @Test
    fun `string generates values with the requested length and charset`() {
        val fiktion =
            Fiktion {
                type<String>() generatesBy {
                    string(length = 12, charset = FiktionCharset.Numeric)
                }
            }

        val value = fiktion.fake<String>(seed = 1)

        assertEquals(12, value.length)
        assertTrue(value.all(Char::isDigit))
    }
}
