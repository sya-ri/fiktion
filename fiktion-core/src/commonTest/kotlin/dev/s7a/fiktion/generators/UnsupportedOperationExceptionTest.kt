package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class UnsupportedOperationExceptionTest {
    @Test
    fun `unsupportedOperationException generates configured message`() {
        val fiktion =
            Fiktion {
                type<UnsupportedOperationException>() generatesBy {
                    unsupportedOperationException(message = "unsupported")
                }
            }

        assertEquals("unsupported", fiktion.fake<UnsupportedOperationException>(seed = 1).message)
    }
}
