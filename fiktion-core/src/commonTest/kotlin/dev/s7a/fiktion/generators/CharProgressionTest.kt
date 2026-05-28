package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class CharProgressionTest {
    @Test
    fun `charProgression generates finite progressions`() {
        val fiktion =
            Fiktion {
                type<CharProgression>() generatesBy { charProgression() }
            }

        assertTrue(fiktion.fake<CharProgression>(seed = 1).toList().isNotEmpty())
    }
}
