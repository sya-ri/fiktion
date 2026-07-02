package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.FiktionConfigurationException
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.generatesOneOf
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
    fun `oneOf chooses from choices after excluding a value`() {
        val fiktion =
            Fiktion {
                type<String>() generatesBy {
                    oneOf(listOf("primary", "secondary") excluding "secondary")
                }
            }

        assertTrue(fiktion.fake<String>(seed = 1) == "primary")
    }

    @Test
    fun `oneOf chooses from choices after excluding by predicate`() {
        val fiktion =
            Fiktion {
                type<String>() generatesBy {
                    oneOf(listOf("primary", "secondary") excluding { value -> value.startsWith("s") })
                }
            }

        assertTrue(fiktion.fake<String>(seed = 1) == "primary")
    }

    @Test
    fun `generatesOneOf excludes a value`() {
        val fiktion =
            Fiktion {
                type<String>() generatesOneOf listOf("primary", "secondary") excluding "secondary"
            }

        assertTrue(fiktion.fake<String>(seed = 1) == "primary")
    }

    @Test
    fun `generatesOneOf excludes multiple values`() {
        val fiktion =
            Fiktion {
                type<String>() generatesOneOf listOf("primary", "secondary", "tertiary") excluding
                    setOf("secondary", "tertiary")
            }

        assertTrue(fiktion.fake<String>(seed = 1) == "primary")
    }

    @Test
    fun `generatesOneOf applies cumulative exclusions`() {
        val fiktion =
            Fiktion {
                type<String>() generatesOneOf listOf("primary", "secondary", "tertiary") excluding "secondary" excluding
                    "tertiary"
            }

        assertTrue(fiktion.fake<String>(seed = 1) == "primary")
    }

    @Test
    fun `generatesOneOf excludes values by predicate`() {
        val fiktion =
            Fiktion {
                type<String>() generatesOneOf listOf("primary", "secondary") excluding { value: String ->
                    value.startsWith("s")
                }
            }

        assertTrue(fiktion.fake<String>(seed = 1) == "primary")
    }

    @Test
    fun `generatesOneOf rejects choices when all values are excluded`() {
        val fiktion =
            Fiktion {
                type<String>() generatesOneOf listOf("primary") excluding "primary"
            }

        assertFailsWith<FiktionConfigurationException> {
            fiktion.fake<String>(seed = 1)
        }
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
