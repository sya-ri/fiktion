package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class ShortTest {
    @Test
    fun `short supports configured ranges`() {
        val fiktion =
            Fiktion {
                type<Short>() generatesBy { short(100, 200) }
            }

        assertTrue(fiktion.fake<Short>(seed = 1) in 100..200)
    }
}
