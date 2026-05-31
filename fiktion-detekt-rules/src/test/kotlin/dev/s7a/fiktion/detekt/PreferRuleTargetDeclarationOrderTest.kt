package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferRuleTargetDeclarationOrderTest {
    private val rule = PreferRuleTargetDeclarationOrder(Config.empty)

    @Test
    fun `reports configs declared after generators in grouped target blocks`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<String>() {
                        this generates "value"
                        this using FiktionConfig.String.length(8)
                    }
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals("Declare configs before generators in this Fiktion target block.", findings.single().message)
    }

    @Test
    fun `does not report configs before generators`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<String>() {
                        this using FiktionConfig.String.length(8)
                        this generates "value"
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `auto corrects simple grouped target block declaration order`() {
        val code =
            """
            fun configure() {
                type<String>() {
                    this generates "value"
                    this using FiktionConfig.String.length(8)
                    this generatesBy { "other" }
                }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferRuleTargetDeclarationOrder(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertEquals(
            """
            fun configure() {
                type<String>() {
                    this using FiktionConfig.String.length(8)
                    this generates "value"
                    this generatesBy { "other" }
                }
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
