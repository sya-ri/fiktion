package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class SetTest {
    @Test
    fun `set generates configured elements`() {
        val fiktion =
            Fiktion {
                type<Set<Int>>() generatesBy { set(size = 3) { int(1, 9) } }
            }

        assertTrue(fiktion.fake<Set<Int>>(seed = 1).isNotEmpty())
    }
}
