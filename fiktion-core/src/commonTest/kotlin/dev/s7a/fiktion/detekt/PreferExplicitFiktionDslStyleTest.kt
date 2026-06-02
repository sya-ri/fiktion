package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.invoke
import dev.s7a.fiktion.using
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferExplicitFiktionDslStyleTest {
    @Test
    fun `bare using call is equivalent to explicit this using declaration`() {
        val bareUsing =
            fake<String> {
                using(FiktionConfig.String.length(5))
            }
        val explicitThisUsing =
            fake<String> {
                this using FiktionConfig.String.length(5)
            }

        assertEquals(bareUsing.length, explicitThisUsing.length)
    }

    @Test
    fun `dot-call DSL declarations are equivalent to infix DSL declarations`() {
        val dotCall =
            Fiktion {
                type<String>().using(FiktionConfig.String.length(5))
                type<String>().generatesBy { "value" }
            }.fake<String>()
        val infix =
            Fiktion {
                type<String>() using FiktionConfig.String.length(5)
                type<String>() generatesBy { "value" }
            }.fake<String>()

        assertEquals(dotCall, infix)
    }

    @Test
    fun `positional fake seed is equivalent to named fake seed`() {
        val positional =
            fake<List<String>>(123) {
                this using FiktionConfig.Collection.size(1)
            }
        val named =
            fake<List<String>>(seed = 123) {
                this using FiktionConfig.Collection.size(1)
            }

        assertEquals(positional, named)
    }

    @Test
    fun `config declarations before generators are equivalent to generators before configs`() {
        val generatorFirst =
            Fiktion {
                type<String> {
                    this generates "value"
                    this using FiktionConfig.String.length(5)
                }
            }.fake<String>()
        val configFirst =
            Fiktion {
                type<String> {
                    this using FiktionConfig.String.length(5)
                    this generates "value"
                }
            }.fake<String>()

        assertEquals(generatorFirst, configFirst)
    }
}
