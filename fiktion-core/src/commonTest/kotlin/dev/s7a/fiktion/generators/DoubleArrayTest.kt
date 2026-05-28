package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class DoubleArrayTest {
    @Test
    fun `doubleArray generates configured size`() {
        val fiktion = Fiktion { type<DoubleArray>() generatesBy { doubleArray(size = 3) } }

        assertEquals(3, fiktion.fake<DoubleArray>(seed = 1).size)
    }
}
