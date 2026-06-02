package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.element
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class AvoidMultipleSeedsInFakeSpecTest {
    @Test
    fun `last withSeed declaration wins in the same fake block`() {
        val multipleSeeds =
            fake<List<String>> {
                withSeed(123)
                withSeed(456)
                this using FiktionConfig.Collection.size(1)
                element generatesBy {
                    "value-$seed"
                }
            }
        val lastSeed =
            fake<List<String>>(seed = 456) {
                this using FiktionConfig.Collection.size(1)
                element generatesBy {
                    "value-$seed"
                }
            }

        assertEquals(lastSeed, multipleSeeds)
    }
}
