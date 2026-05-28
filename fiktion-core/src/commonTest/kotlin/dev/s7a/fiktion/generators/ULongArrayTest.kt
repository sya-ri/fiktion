@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class ULongArrayTest {
    @Test
    fun `ulongArray generates configured size`() {
        val fiktion = Fiktion { type<ULongArray>() generatesBy { ulongArray(size = 3) } }

        assertEquals(3, fiktion.fake<ULongArray>(seed = 1).size)
    }
}
