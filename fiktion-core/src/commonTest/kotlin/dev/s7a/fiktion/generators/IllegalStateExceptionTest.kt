package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class IllegalStateExceptionTest {
    @Test
    fun `illegalStateException generates configured message`() {
        val fiktion =
            Fiktion { type<IllegalStateException>() generatesBy { illegalStateException(message = "state") } }

        assertEquals("state", fiktion.fake<IllegalStateException>(seed = 1).message)
    }
}
