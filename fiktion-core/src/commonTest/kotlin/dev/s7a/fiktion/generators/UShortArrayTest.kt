@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class UShortArrayTest {
    @Test
    fun `ushortArray generates configured size`() {
        val fiktion = Fiktion { type<UShortArray>() generatesBy { ushortArray(size = 3) } }

        assertEquals(3, fiktion.fake<UShortArray>(seed = 1).size)
    }
}
