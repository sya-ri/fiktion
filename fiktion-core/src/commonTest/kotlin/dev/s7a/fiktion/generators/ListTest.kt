package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class ListTest {
    @Test
    fun `list generates configured elements`() {
        val fiktion =
            Fiktion {
                type<List<Int>>() generatesBy { list(size = 3) { int(1, 9) } }
            }

        assertEquals(3, fiktion.fake<List<Int>>(seed = 1).size)
    }
}
