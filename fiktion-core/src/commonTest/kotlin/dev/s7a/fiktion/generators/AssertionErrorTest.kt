package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class AssertionErrorTest {
    @Test
    fun `assertionError generates configured message`() {
        val fiktion = Fiktion { type<AssertionError>() generatesBy { assertionError(message = "assertion") } }

        assertEquals("assertion", fiktion.fake<AssertionError>(seed = 1).message)
    }
}
