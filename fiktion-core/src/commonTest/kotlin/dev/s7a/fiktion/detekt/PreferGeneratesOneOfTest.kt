package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.generatesOneOf
import dev.s7a.fiktion.generators.oneOf
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferGeneratesOneOfTest {
    @Test
    fun `generatesOneOf rule is equivalent to oneOf generator rule`() {
        val oneOfGenerator =
            Fiktion {
                type<String>() generatesBy { oneOf("a", "b", "c") }
            }
        val generatesOneOf =
            Fiktion {
                type<String>() generatesOneOf listOf("a", "b", "c")
            }

        val seeds = listOf(1L, 2L, 3L, 4L, 5L)
        assertEquals(
            seeds.map { seed -> oneOfGenerator.fake<String>(seed = seed) },
            seeds.map { seed -> generatesOneOf.fake<String>(seed = seed) },
        )
    }
}
