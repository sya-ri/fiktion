package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class ArithmeticExceptionTest {
    @Test
    fun `arithmeticException generates configured message`() {
        val fiktion = Fiktion { type<ArithmeticException>() generatesBy { arithmeticException(message = "arithmetic") } }

        assertEquals("arithmetic", fiktion.fake<ArithmeticException>(seed = 1).message)
    }
}
