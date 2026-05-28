package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class UByteTest {
    @Test
    fun `ubyte supports configured ranges`() {
        val fiktion =
            Fiktion {
                type<UByte>() generatesBy { ubyte(10u, 20u) }
            }

        assertTrue(fiktion.fake<UByte>(seed = 1) in 10u..20u)
    }
}
