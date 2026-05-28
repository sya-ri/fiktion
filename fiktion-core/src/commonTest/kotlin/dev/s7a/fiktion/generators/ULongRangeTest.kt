package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertFalse

class ULongRangeTest {
    @Test
    fun `ulongRange generates non-empty ranges`() {
        val fiktion =
            Fiktion {
                type<ULongRange>() generatesBy { ulongRange() }
            }

        assertFalse(fiktion.fake<ULongRange>(seed = 1).isEmpty())
    }
}
