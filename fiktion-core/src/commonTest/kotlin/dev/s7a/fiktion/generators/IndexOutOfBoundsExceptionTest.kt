package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class IndexOutOfBoundsExceptionTest {
    @Test
    fun `indexOutOfBoundsException generates configured message`() {
        val fiktion =
            Fiktion { type<IndexOutOfBoundsException>() generatesBy { indexOutOfBoundsException(message = "index") } }

        assertEquals("index", fiktion.fake<IndexOutOfBoundsException>(seed = 1).message)
    }
}
