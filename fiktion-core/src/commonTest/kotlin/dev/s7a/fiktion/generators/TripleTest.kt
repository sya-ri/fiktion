package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class TripleTest {
    @Test
    fun `triple generates all parts`() {
        val fiktion =
            Fiktion {
                type<Triple<String, Int, Boolean>>() generatesBy {
                    triple(first = { "first" }, second = { 2 }, third = { true })
                }
            }

        assertEquals(Triple("first", 2, true), fiktion.fake<Triple<String, Int, Boolean>>(seed = 1))
    }
}
