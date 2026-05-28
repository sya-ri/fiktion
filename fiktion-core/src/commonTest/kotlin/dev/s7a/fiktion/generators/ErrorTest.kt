package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorTest {
    @Test
    fun `error generates configured message`() {
        val fiktion = Fiktion { type<Error>() generatesBy { error(message = "error") } }

        assertEquals("error", fiktion.fake<Error>(seed = 1).message)
    }
}
