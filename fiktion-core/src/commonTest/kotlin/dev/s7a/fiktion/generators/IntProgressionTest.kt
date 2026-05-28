package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class IntProgressionTest {
    @Test
    fun `intProgression generates finite progressions`() {
        val fiktion =
            Fiktion {
                type<IntProgression>() generatesBy { intProgression() }
            }

        assertTrue(fiktion.fake<IntProgression>(seed = 1).toList().isNotEmpty())
    }
}
