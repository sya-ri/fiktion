package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertFalse

class LongRangeTest {
    @Test
    fun `longRange generates non-empty ranges`() {
        val fiktion =
            Fiktion {
                type<LongRange>() generatesBy { longRange() }
            }

        assertFalse(fiktion.fake<LongRange>(seed = 1).isEmpty())
    }
}
