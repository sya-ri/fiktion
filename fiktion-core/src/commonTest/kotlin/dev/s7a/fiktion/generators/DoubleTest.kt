package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class DoubleTest {
    @Test
    fun `double supports configured ranges`() {
        val fiktion =
            Fiktion {
                type<Double>() generatesBy { double(1.5, 2.5) }
            }

        assertTrue(fiktion.fake<Double>(seed = 1) in 1.5..2.5)
    }
}
