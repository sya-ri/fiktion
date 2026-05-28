package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.FiktionConfigurationException
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class OneOfTest {
    @Test
    fun `oneOf chooses a value from the provided choices`() {
        val fiktion =
            Fiktion {
                type<String>() generatesBy {
                    oneOf("primary", "secondary")
                }
            }

        assertTrue(fiktion.fake<String>(seed = 1) in setOf("primary", "secondary"))
    }

    @Test
    fun `oneOf rejects empty choices`() {
        val fiktion =
            Fiktion {
                type<String>() generatesBy {
                    oneOf(emptyList())
                }
            }

        assertFailsWith<FiktionConfigurationException> {
            fiktion.fake<String>(seed = 1)
        }
    }
}
