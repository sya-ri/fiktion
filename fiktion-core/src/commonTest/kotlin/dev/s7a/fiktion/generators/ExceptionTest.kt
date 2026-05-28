package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class ExceptionTest {
    @Test
    fun `exception generates configured message`() {
        val fiktion = Fiktion { type<Exception>() generatesBy { exception(message = "exception") } }

        assertEquals("exception", fiktion.fake<Exception>(seed = 1).message)
    }
}
