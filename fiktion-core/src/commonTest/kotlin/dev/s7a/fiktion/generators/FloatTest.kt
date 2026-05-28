package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class FloatTest {
    @Test
    fun `float supports configured ranges`() {
        val fiktion =
            Fiktion {
                type<Float>() generatesBy { float(1.5f, 2.5f) }
            }

        assertTrue(fiktion.fake<Float>(seed = 1) in 1.5f..2.5f)
    }
}
