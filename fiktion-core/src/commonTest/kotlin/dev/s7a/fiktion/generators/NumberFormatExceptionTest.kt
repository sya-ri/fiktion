package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatExceptionTest {
    @Test
    fun `numberFormatException generates configured message`() {
        val fiktion = Fiktion { type<NumberFormatException>() generatesBy { numberFormatException(message = "number") } }

        assertEquals("number", fiktion.fake<NumberFormatException>(seed = 1).message)
    }
}
