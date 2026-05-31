package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class PreferGeneratesByForMutableValuesTest {
    @Test
    fun `mutable fixed value rule reuses an instance while mutable generator rule creates instances`() {
        val fixed =
            Fiktion {
                type<MutableList<String>>() generates mutableListOf("value")
            }
        val generator =
            Fiktion {
                type<MutableList<String>>() generatesBy { mutableListOf("value") }
            }

        val fixedFirst = fixed.fake<MutableList<String>>()
        val fixedSecond = fixed.fake<MutableList<String>>()
        val generatorFirst = generator.fake<MutableList<String>>()
        val generatorSecond = generator.fake<MutableList<String>>()

        assertEquals(fixedFirst, generatorFirst)
        assertSame(fixedFirst, fixedSecond)
        assertNotSame(generatorFirst, generatorSecond)
    }
}
