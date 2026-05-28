package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class MutableListTest {
    @Test
    fun `mutableList generates configured elements`() {
        val fiktion =
            Fiktion {
                type<MutableList<Int>>() generatesBy { mutableList(size = 3) { int(1, 9) } }
            }

        assertEquals(3, fiktion.fake<MutableList<Int>>(seed = 1).size)
    }
}
