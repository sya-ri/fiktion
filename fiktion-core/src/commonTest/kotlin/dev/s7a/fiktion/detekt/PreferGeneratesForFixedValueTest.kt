package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.generatesOneOf
import dev.s7a.fiktion.generators.oneOf
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferGeneratesForFixedValueTest {
    @Test
    fun `fixed value rule is equivalent to fixed generator rules`() {
        val fixed =
            Fiktion {
                type<Int>() generates 1
            }
        val constantGenerator =
            Fiktion {
                type<Int>() generatesBy { 1 }
            }
        val singleOneOf =
            Fiktion {
                type<Int>() generatesBy { oneOf(1) }
            }
        val singleListGeneratesOneOf =
            Fiktion {
                type<Int>() generatesOneOf listOf(1)
            }
        val singleSetGeneratesOneOf =
            Fiktion {
                type<Int>() generatesOneOf setOf(1)
            }
        val seeds = listOf(0L, 1L, 2L, 100L)

        assertEquals(
            seeds.map { seed -> fixed.fake<Int>(seed = seed) },
            seeds.map { seed -> constantGenerator.fake<Int>(seed = seed) },
        )
        assertEquals(
            seeds.map { seed -> fixed.fake<Int>(seed = seed) },
            seeds.map { seed -> singleOneOf.fake<Int>(seed = seed) },
        )
        assertEquals(
            seeds.map { seed -> fixed.fake<Int>(seed = seed) },
            seeds.map { seed -> singleListGeneratesOneOf.fake<Int>(seed = seed) },
        )
        assertEquals(
            seeds.map { seed -> fixed.fake<Int>(seed = seed) },
            seeds.map { seed -> singleSetGeneratesOneOf.fake<Int>(seed = seed) },
        )
    }
}
