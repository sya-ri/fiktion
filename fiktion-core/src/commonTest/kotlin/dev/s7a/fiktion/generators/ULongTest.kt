package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class ULongTest {
    @Test
    fun `ulong supports configured ranges`() {
        val fiktion =
            Fiktion {
                type<ULong>() generatesBy { ulong(1_000uL, 2_000uL) }
            }

        assertTrue(fiktion.fake<ULong>(seed = 1) in 1_000uL..2_000uL)
    }
}
