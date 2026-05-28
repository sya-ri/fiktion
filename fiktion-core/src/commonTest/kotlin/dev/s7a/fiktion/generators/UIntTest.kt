package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class UIntTest {
    @Test
    fun `uint supports configured ranges`() {
        val fiktion =
            Fiktion {
                type<UInt>() generatesBy { uint(1_000u, 2_000u) }
            }

        assertTrue(fiktion.fake<UInt>(seed = 1) in 1_000u..2_000u)
    }
}
