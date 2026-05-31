package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.FiktionConfigurationException
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.invoke
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AvoidInvalidConfigRangeTest {
    @Test
    fun `empty string length config range fails generation`() {
        assertFailsWith<FiktionConfigurationException> {
            fake<String> {
                this using FiktionConfig.String.length(10..1)
            }
        }
    }

    @Test
    fun `negative collection size config fails generation`() {
        assertFailsWith<IllegalArgumentException> {
            fake<List<String>> {
                this using FiktionConfig.Collection.size(-1)
            }
        }
    }
}
