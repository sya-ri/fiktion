package dev.s7a.fiktion

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ChildContextTest {
    @Test
    fun `child context exposes child index depth and seed`() {
        val fiktion =
            Fiktion {
                type<List<Long>>() generatesBy {
                    val first = childContext(index = 0)
                    val second = childContext(index = 1)
                    listOf(first.index.toLong(), second.index.toLong(), first.depth.toLong(), first.seed, second.seed)
                }
            }

        val values = fiktion.fake<List<Long>>(seed = 123)

        assertEquals(0L, values[0])
        assertEquals(1L, values[1])
        assertEquals(1L, values[2])
        assertNotEquals(values[3], values[4])
    }

    @Test
    fun `child context can separate public index from seed index`() {
        val fiktion =
            Fiktion {
                type<Pair<Long, Long>>() generatesBy {
                    childContext(index = 0, seedIndex = 1).seed to childContext(index = 1, seedIndex = 1).seed
                }
            }

        assertEquals(
            fiktion.fake<Pair<Long, Long>>(seed = 123).first,
            fiktion.fake<Pair<Long, Long>>(seed = 123).second,
        )
    }
}
