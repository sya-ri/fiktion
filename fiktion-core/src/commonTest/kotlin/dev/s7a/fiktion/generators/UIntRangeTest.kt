package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertFalse

class UIntRangeTest {
    @Test
    fun `uintRange generates non-empty ranges`() {
        val fiktion =
            Fiktion {
                type<UIntRange>() generatesBy { uintRange() }
            }

        assertFalse(fiktion.fake<UIntRange>(seed = 1).isEmpty())
    }
}
