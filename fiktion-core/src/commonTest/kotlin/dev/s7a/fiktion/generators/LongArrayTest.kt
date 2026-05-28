package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class LongArrayTest {
    @Test
    fun `longArray generates configured size`() {
        val fiktion = Fiktion { type<LongArray>() generatesBy { longArray(size = 3) } }

        assertEquals(3, fiktion.fake<LongArray>(seed = 1).size)
    }
}
