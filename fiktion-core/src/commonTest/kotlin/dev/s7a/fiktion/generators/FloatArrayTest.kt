package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class FloatArrayTest {
    @Test
    fun `floatArray generates configured size`() {
        val fiktion = Fiktion { type<FloatArray>() generatesBy { floatArray(size = 3) } }

        assertEquals(3, fiktion.fake<FloatArray>(seed = 1).size)
    }
}
