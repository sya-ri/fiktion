package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.element
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.invoke
import dev.s7a.fiktion.using
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferFixedConfigValueTest {
    @Test
    fun `fixed range config is equivalent to equal-bound range config`() {
        val fixed =
            fake<List<Int>>(seed = 1) {
                this using FiktionConfig.Collection.size(2)
                element using FiktionConfig.Int.range(42)
            }
        val equalBoundRange =
            fake<List<Int>>(seed = 1) {
                this using FiktionConfig.Collection.size(2..2)
                element using FiktionConfig.Int.range(42..42)
            }

        assertEquals(equalBoundRange, fixed)
    }
}
