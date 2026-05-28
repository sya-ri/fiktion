package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class ThrowableTest {
    @Test
    fun `throwable generates configured message`() {
        val fiktion = Fiktion { type<Throwable>() generatesBy { throwable(message = "failure") } }

        assertEquals("failure", fiktion.fake<Throwable>(seed = 1).message)
    }
}
