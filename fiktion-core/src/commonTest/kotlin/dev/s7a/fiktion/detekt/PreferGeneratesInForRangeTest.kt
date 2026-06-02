package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.generatesIn
import dev.s7a.fiktion.generators.int
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferGeneratesInForRangeTest {
    @Test
    fun `generatesIn rule is equivalent to range generator rule`() {
        val rangeGenerator =
            Fiktion {
                type<Int>() generatesBy { int(1..10) }
            }
        val generatesIn =
            Fiktion {
                type<Int>() generatesIn 1..10
            }

        val seeds = listOf(1L, 2L, 3L, 4L, 5L)
        assertEquals(
            seeds.map { seed -> rangeGenerator.fake<Int>(seed = seed) },
            seeds.map { seed -> generatesIn.fake<Int>(seed = seed) },
        )
    }
}
