package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertTrue

class MapTest {
    @Test
    fun `map generates configured entries`() {
        val fiktion =
            Fiktion {
                type<Map<String, Int>>() generatesBy {
                    map(
                        size = 3,
                        key = { string() },
                        value = { int(1, 9) },
                    )
                }
            }

        assertTrue(fiktion.fake<Map<String, Int>>(seed = 1).isNotEmpty())
    }
}
