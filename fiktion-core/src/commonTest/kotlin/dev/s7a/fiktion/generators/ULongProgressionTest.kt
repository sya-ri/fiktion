package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class ULongProgressionTest {
    @Test
    fun `ulongProgression generates finite progressions`() {
        val fiktion =
            Fiktion {
                type<ULongProgression>() generatesBy { ulongProgression() }
            }

        assertTrue(fiktion.fake<ULongProgression>(seed = 1).toList().isNotEmpty())
    }
}
