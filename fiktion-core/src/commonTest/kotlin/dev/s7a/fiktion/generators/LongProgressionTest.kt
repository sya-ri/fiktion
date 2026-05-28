package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class LongProgressionTest {
    @Test
    fun `longProgression generates finite progressions`() {
        val fiktion =
            Fiktion {
                type<LongProgression>() generatesBy { longProgression() }
            }

        assertTrue(fiktion.fake<LongProgression>(seed = 1).toList().isNotEmpty())
    }
}
