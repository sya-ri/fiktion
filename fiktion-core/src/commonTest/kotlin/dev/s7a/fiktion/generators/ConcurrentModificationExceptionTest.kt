package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class ConcurrentModificationExceptionTest {
    @Test
    fun `concurrentModificationException generates configured message`() {
        val fiktion =
            Fiktion {
                type<ConcurrentModificationException>() generatesBy {
                    concurrentModificationException(message = "concurrent")
                }
            }

        assertEquals("concurrent", fiktion.fake<ConcurrentModificationException>(seed = 1).message)
    }
}
