package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.UniqueElementStrategy
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.invoke
import dev.s7a.fiktion.using
import kotlin.test.Test
import kotlin.test.assertEquals
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

    @Test
    fun `mutableSet retries duplicate elements when exact unique element generation is configured`() {
        val fiktion =
            Fiktion {
                type<MutableSet<Int>> {
                    this using FiktionConfig.Collection.uniqueElementStrategy(UniqueElementStrategy.Exact(maxAttemptsPerElement = 2))
                    this generatesBy { mutableSet(size = 3) { index / 2 } }
                }
            }

        assertEquals(mutableSetOf(0, 1, 2), fiktion.fake<MutableSet<Int>>(seed = 1))
    }
}
