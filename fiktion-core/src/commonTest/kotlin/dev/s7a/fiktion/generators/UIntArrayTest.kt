@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class UIntArrayTest {
    @Test
    fun `uintArray generates configured size`() {
        val fiktion = Fiktion { type<UIntArray>() generatesBy { uintArray(size = 3) } }

        assertEquals(3, fiktion.fake<UIntArray>(seed = 1).size)
    }
}
