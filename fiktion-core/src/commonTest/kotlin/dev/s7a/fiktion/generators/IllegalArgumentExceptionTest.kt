package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class IllegalArgumentExceptionTest {
    @Test
    fun `illegalArgumentException generates configured message`() {
        val fiktion =
            Fiktion { type<IllegalArgumentException>() generatesBy { illegalArgumentException(message = "argument") } }

        assertEquals("argument", fiktion.fake<IllegalArgumentException>(seed = 1).message)
    }
}
