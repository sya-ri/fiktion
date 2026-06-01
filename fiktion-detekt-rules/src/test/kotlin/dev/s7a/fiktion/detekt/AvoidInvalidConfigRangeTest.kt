package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.test.lint
import kotlin.test.Test
import kotlin.test.assertEquals

class AvoidInvalidConfigRangeTest {
    private val rule = AvoidInvalidConfigRange(Config.empty)

    @Test
    fun `reports empty and negative Fiktion config ranges`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<String>() using FiktionConfig.String.length(10..1)
                    type<List<String>>() using FiktionConfig.Collection.size(-1..3)
                    type<List<String>>() using FiktionConfig.Collection.size(-1)
                    type<IntProgression>() using FiktionConfig.IntProgression.step(0)
                }
                """.trimIndent(),
            )

        assertEquals(4, findings.size)
        assertEquals(
            "Avoid invalid Fiktion config `FiktionConfig.String.length(10..1)`: range must not be empty.",
            findings[0].message,
        )
    }

    @Test
    fun `does not report valid config ranges`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<String>() using FiktionConfig.String.length(1..10)
                    type<List<String>>() using FiktionConfig.Collection.size(0..3)
                    type<IntProgression>() using FiktionConfig.IntProgression.step(1..5)
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }
}
