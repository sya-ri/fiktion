package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class ClassCastExceptionTest {
    @Test
    fun `classCastException generates configured message`() {
        val fiktion = Fiktion { type<ClassCastException>() generatesBy { classCastException(message = "cast") } }

        assertEquals("cast", fiktion.fake<ClassCastException>(seed = 1).message)
    }
}
