package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class BooleanArrayTest {
    @Test
    fun `booleanArray generates configured size`() {
        val fiktion = Fiktion { type<BooleanArray>() generatesBy { booleanArray(size = 3) } }

        assertEquals(3, fiktion.fake<BooleanArray>(seed = 1).size)
    }
}
