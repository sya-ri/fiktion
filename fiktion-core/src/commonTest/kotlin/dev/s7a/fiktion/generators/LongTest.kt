package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class LongTest {
    @Test
    fun `long supports configured ranges`() {
        val fiktion =
            Fiktion {
                type<Long>() generatesBy { long(1_000L, 2_000L) }
            }

        assertTrue(fiktion.fake<Long>(seed = 1) in 1_000L..2_000L)
    }
}
