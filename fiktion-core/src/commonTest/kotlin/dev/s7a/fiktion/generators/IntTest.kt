package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IntTest {
    @Test
    fun `int uses the current fake context seed`() {
        val fiktion =
            Fiktion {
                type<Int>() generatesBy { int() }
            }

        assertEquals(fiktion.fake<Int>(seed = 123), fiktion.fake<Int>(seed = 123))
    }

    @Test
    fun `int supports configured ranges`() {
        val fiktion =
            Fiktion {
                type<Int>() generatesBy { int(1_000, 2_000) }
            }

        assertTrue(fiktion.fake<Int>(seed = 1) in 1_000..2_000)
    }
}
