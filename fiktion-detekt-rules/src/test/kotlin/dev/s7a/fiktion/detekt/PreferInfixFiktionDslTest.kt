package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferInfixFiktionDslTest {
    private val rule = PreferInfixFiktionDsl(Config.empty)

    @Test
    fun `reports dot-call Fiktion DSL declarations`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<String>().generates("value")
                    property(User::id).generatesBy { "id" }
                    this.using(FiktionConfig.String.length(8))
                }
                """.trimIndent(),
            )

        assertEquals(3, findings.size)
        assertEquals(
            "Use `type<String>() generates \"value\"` instead of `type<String>().generates(\"value\")`.",
            findings[0].message,
        )
        assertEquals(
            "Use `property(User::id) generatesBy { \"id\" }` instead of `property(User::id).generatesBy { \"id\" }`.",
            findings[1].message,
        )
        assertEquals(
            "Use `this using FiktionConfig.String.length(8)` instead of `this.using(FiktionConfig.String.length(8))`.",
            findings[2].message,
        )
    }

    @Test
    fun `does not report non-Fiktion receivers or already infix declarations`() {
        val findings =
            rule.lint(
                """
                fun configure(target: OtherTarget) {
                    target.generates("value")
                    type<String>() generates "value"
                    this using FiktionConfig.String.length(8)
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `auto corrects dot-call Fiktion DSL declarations`() {
        val code =
            """
            fun configure() {
                type<String>().generates("value")
                property(User::id).generatesBy { "id" }
                this.using(FiktionConfig.String.length(8))
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferInfixFiktionDsl(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(3, findings.size)
        assertEquals(
            """
            fun configure() {
                type<String>() generates "value"
                property(User::id) generatesBy { "id" }
                this using FiktionConfig.String.length(8)
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
