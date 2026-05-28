package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class UIntProgressionTest {
    @Test
    fun `uintProgression generates finite progressions`() {
        val fiktion =
            Fiktion {
                type<UIntProgression>() generatesBy { uintProgression() }
            }

        assertTrue(fiktion.fake<UIntProgression>(seed = 1).toList().isNotEmpty())
    }
}
