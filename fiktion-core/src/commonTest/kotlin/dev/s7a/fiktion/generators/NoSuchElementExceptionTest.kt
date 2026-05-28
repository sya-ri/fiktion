package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class NoSuchElementExceptionTest {
    @Test
    fun `noSuchElementException generates configured message`() {
        val fiktion =
            Fiktion { type<NoSuchElementException>() generatesBy { noSuchElementException(message = "element") } }

        assertEquals("element", fiktion.fake<NoSuchElementException>(seed = 1).message)
    }
}
