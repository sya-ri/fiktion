package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class CharArrayTest {
    @Test
    fun `charArray generates configured size`() {
        val fiktion = Fiktion { type<CharArray>() generatesBy { charArray(size = 3) } }

        assertEquals(3, fiktion.fake<CharArray>(seed = 1).size)
    }
}
