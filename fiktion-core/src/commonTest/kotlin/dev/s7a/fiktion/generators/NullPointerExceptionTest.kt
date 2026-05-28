package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class NullPointerExceptionTest {
    @Test
    fun `nullPointerException generates configured message`() {
        val fiktion =
            Fiktion { type<NullPointerException>() generatesBy { nullPointerException(message = "null") } }

        assertEquals("null", fiktion.fake<NullPointerException>(seed = 1).message)
    }
}
