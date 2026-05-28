package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class IntArrayTest {
    @Test
    fun `intArray generates configured size`() {
        val fiktion = Fiktion { type<IntArray>() generatesBy { intArray(size = 3) } }

        assertEquals(3, fiktion.fake<IntArray>(seed = 1).size)
    }
}
