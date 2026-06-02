package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferThisUsingInFakeSpecTest {
    private val rule = PreferThisUsingInFakeSpec(Config.empty)

    @Test
    fun `reports bare using calls`() {
        val findings =
            rule.lint(
                """
                fun test() {
                    fake<String> {
                        using(FiktionConfig.String.length(8))
                    }
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals(
            "Use `this using FiktionConfig.String.length(8)` instead of `using(FiktionConfig.String.length(8))`.",
            findings.single().message,
        )
    }

    @Test
    fun `does not report explicit receiver using calls`() {
        val findings =
            rule.lint(
                """
                fun test() {
                    fake<String> {
                        this using FiktionConfig.String.length(8)
                        this.using(FiktionConfig.String.length(8))
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `auto corrects bare using calls`() {
        val code =
            """
            fun test() {
                fake<String> {
                    using(FiktionConfig.String.length(8))
                }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferThisUsingInFakeSpec(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertEquals(
            """
            fun test() {
                fake<String> {
                    this using FiktionConfig.String.length(8)
                }
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
