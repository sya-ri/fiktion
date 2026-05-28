package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class SequenceTest {
    @Test
    fun `sequence generates configured elements`() {
        val fiktion =
            Fiktion {
                type<Sequence<Int>>() generatesBy { sequence(size = 3) { int(1, 9) } }
            }

        assertEquals(3, fiktion.fake<Sequence<Int>>(seed = 1).toList().size)
    }
}
