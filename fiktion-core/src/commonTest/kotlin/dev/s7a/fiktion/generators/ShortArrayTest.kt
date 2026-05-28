package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class ShortArrayTest {
    @Test
    fun `shortArray generates configured size`() {
        val fiktion = Fiktion { type<ShortArray>() generatesBy { shortArray(size = 3) } }

        assertEquals(3, fiktion.fake<ShortArray>(seed = 1).size)
    }
}
