package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class UShortTest {
    @Test
    fun `ushort supports configured ranges`() {
        val fiktion =
            Fiktion {
                type<UShort>() generatesBy { ushort(100u, 200u) }
            }

        assertTrue(fiktion.fake<UShort>(seed = 1) in 100u..200u)
    }
}
