package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class MutableSetTest {
    @Test
    fun `mutableSet generates configured elements`() {
        val fiktion =
            Fiktion {
                type<MutableSet<Int>>() generatesBy { mutableSet(size = 3) { int(1, 9) } }
            }

        assertTrue(fiktion.fake<MutableSet<Int>>(seed = 1).isNotEmpty())
    }
}
