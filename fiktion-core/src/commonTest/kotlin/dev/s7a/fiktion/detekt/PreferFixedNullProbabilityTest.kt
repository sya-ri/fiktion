package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PreferFixedNullProbabilityTest {
    @Test
    fun `zero null probability is equivalent to the fixed generated value`() {
        val fixed =
            Fiktion {
                type<String?>() generates "value"
            }
        val zeroNullProbability =
            Fiktion {
                type<String?>() generates "value" orNullAt 0.0
            }

        val seeds = listOf(0L, 1L, 2L, 100L)

        assertEquals(
            seeds.map { seed -> fixed.fake<String?>(seed = seed) },
            seeds.map { seed -> zeroNullProbability.fake<String?>(seed = seed) },
        )
    }

    @Test
    fun `full null probability is equivalent to a fixed null value`() {
        val fixedNull =
            Fiktion {
                type<String?>() generates null
            }
        val fullNullProbability =
            Fiktion {
                type<String?>() generates "value" orNullAt 1.0
            }

        val seeds = listOf(0L, 1L, 2L, 100L)

        seeds.forEach { seed ->
            assertNull(fixedNull.fake<String?>(seed = seed))
            assertEquals(fixedNull.fake<String?>(seed = seed), fullNullProbability.fake<String?>(seed = seed))
        }
    }
}
