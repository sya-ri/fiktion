package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertFalse

class IntRangeTest {
    @Test
    fun `intRange generates non-empty ranges`() {
        val fiktion =
            Fiktion {
                type<IntRange>() generatesBy { intRange() }
            }

        assertFalse(fiktion.fake<IntRange>(seed = 1).isEmpty())
    }
}
