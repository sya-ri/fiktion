package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class ByteTest {
    @Test
    fun `byte supports configured ranges`() {
        val fiktion =
            Fiktion {
                type<Byte>() generatesBy { byte(10, 20) }
            }

        assertTrue(fiktion.fake<Byte>(seed = 1) in 10..20)
    }
}
