package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class RuntimeExceptionTest {
    @Test
    fun `runtimeException generates configured message`() {
        val fiktion = Fiktion { type<RuntimeException>() generatesBy { runtimeException(message = "runtime") } }

        assertEquals("runtime", fiktion.fake<RuntimeException>(seed = 1).message)
    }
}
