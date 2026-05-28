package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class PairTest {
    @Test
    fun `pair generates both parts`() {
        val fiktion =
            Fiktion {
                type<Pair<String, Int>>() generatesBy {
                    pair(first = { "first" }, second = { 2 })
                }
            }

        assertEquals("first" to 2, fiktion.fake<Pair<String, Int>>(seed = 1))
    }
}
