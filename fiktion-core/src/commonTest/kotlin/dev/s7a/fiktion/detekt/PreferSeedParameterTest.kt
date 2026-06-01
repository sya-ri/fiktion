package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.element
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferSeedParameterTest {
    @Test
    fun `fake seed parameter is equivalent to leading withSeed in fake block`() {
        val withSeedBlock =
            fake<List<String>> {
                withSeed(123)
                this using FiktionConfig.Collection.size(1)
                element generatesBy {
                    "value-$seed"
                }
            }
        val seedParameter =
            fake<List<String>>(seed = 123) {
                this using FiktionConfig.Collection.size(1)
                element generatesBy {
                    "value-$seed"
                }
            }

        assertEquals(withSeedBlock, seedParameter)
    }
}
