package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferFixedConfigValueTest {
    private val rule = PreferFixedConfigValue(Config.empty)

    @Test
    fun `reports equal fixed ranges in Fiktion config calls`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<List<String>> {
                        this using FiktionConfig.Collection.size(2..2)
                    }
                    type<Int>() using FiktionConfig.Int.range(10..10)
                }
                """.trimIndent(),
            )

        assertEquals(2, findings.size)
        assertEquals(
            "Use `FiktionConfig.Collection.size(2)` instead of `FiktionConfig.Collection.size(2..2)`.",
            findings[0].message,
        )
        assertEquals(
            "Use `FiktionConfig.Int.range(10)` instead of `FiktionConfig.Int.range(10..10)`.",
            findings[1].message,
        )
    }

    @Test
    fun `reports equal fixed ranges in aliased Fiktion config calls`() {
        val findings =
            rule.lint(
                """
                import dev.s7a.fiktion.FiktionConfig as Config

                fun configure() {
                    type<List<String>>() using Config.Collection.size(2..2)
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals(
            "Use `Config.Collection.size(2)` instead of `Config.Collection.size(2..2)`.",
            findings[0].message,
        )
    }

    @Test
    fun `does not report real ranges`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<List<String>>() using FiktionConfig.Collection.size(1..5)
                    type<Int>() using FiktionConfig.Int.range(10..20)
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `does not report non Fiktion config calls`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    other.Config.size(2..2)
                    size(2..2)
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `auto corrects equal fixed ranges to fixed values`() {
        val code =
            """
            fun configure() {
                type<List<String>> {
                    this using FiktionConfig.Collection.size(2..2)
                }
                type<Int>() using FiktionConfig.Int.range(10..10)
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferFixedConfigValue(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(2, findings.size)
        assertEquals(
            """
            fun configure() {
                type<List<String>> {
                    this using FiktionConfig.Collection.size(2)
                }
                type<Int>() using FiktionConfig.Int.range(10)
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
