package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AvoidMultipleConfigsForRuleTargetTest {
    private val rule = AvoidMultipleConfigsForRuleTarget(Config.empty)

    @Test
    fun `reports multiple configs for the same target and config key in the same scope`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<String>() using FiktionConfig.String.length(4)
                    type<String>() using FiktionConfig.String.length(8)

                    property(User::name) using FiktionConfig.String.length(4)
                    property(User::name).using(FiktionConfig.String.length(8))

                    User::email using FiktionConfig.String.length(12)
                    User::email using FiktionConfig.String.length(24)

                    name<String>("email") using FiktionConfig.String.length(12)
                    name<String>("email") using FiktionConfig.String.length(24)
                }
                """.trimIndent(),
            )

        assertEquals(4, findings.size)
        assertEquals(
            "Avoid multiple configs for `type<String>()` with `FiktionConfig.String.length` in the same scope.",
            findings[0].message,
        )
        assertEquals(
            "Avoid multiple configs for `property(User::name)` with `FiktionConfig.String.length` in the same scope.",
            findings[1].message,
        )
        assertEquals(
            "Avoid multiple configs for `User::email` with `FiktionConfig.String.length` in the same scope.",
            findings[2].message,
        )
        assertEquals(
            "Avoid multiple configs for `name<String>(\"email\")` with `FiktionConfig.String.length` in the same scope.",
            findings[3].message,
        )
    }

    @Test
    fun `reports multiple configs inside grouped target blocks`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<String>() {
                        this using FiktionConfig.String.length(4)
                        this using FiktionConfig.String.length(8)
                    }
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals(
            "Avoid multiple configs for `this` with `FiktionConfig.String.length` in the same scope.",
            findings.single().message,
        )
    }

    @Test
    fun `reports multiple configs inside Fiktion blocks`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    Fiktion {
                        type<String>() using FiktionConfig.String.length(4)
                        type<String>() using FiktionConfig.String.length(8)
                    }

                    Fiktion.configure {
                        type<Int>() using FiktionConfig.Int.range(0..10)
                        type<Int>() using FiktionConfig.Int.range(20..30)
                    }
                }
                """.trimIndent(),
            )

        assertEquals(2, findings.size)
        assertEquals(
            "Avoid multiple configs for `type<String>()` with `FiktionConfig.String.length` in the same scope.",
            findings[0].message,
        )
        assertEquals(
            "Avoid multiple configs for `type<Int>()` with `FiktionConfig.Int.range` in the same scope.",
            findings[1].message,
        )
    }

    @Test
    fun `does not report different targets config keys scopes or variable settings`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    val length = FiktionConfig.String.length(8)

                    type<String>() using FiktionConfig.String.length(4)
                    type<Int>() using FiktionConfig.Int.range(0..10)
                    type<String>() using FiktionConfig.String.allowedChars('a'..'z')
                    type<String>() using length

                    type<String>() {
                        this using FiktionConfig.String.length(4)
                    }
                    type<String>() {
                        this using FiktionConfig.String.length(8)
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `uses import aliases in config key text`() {
        val findings =
            rule.lint(
                """
                import dev.s7a.fiktion.FiktionConfig as Config

                fun configure() {
                    type<String>() using Config.String.length(4)
                    type<String>() using Config.String.length(8)
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals(
            "Avoid multiple configs for `type<String>()` with `Config.String.length` in the same scope.",
            findings.single().message,
        )
    }

    @Test
    fun `does not autocorrect multiple configs`() {
        val code =
            """
            fun configure() {
                type<String>() using FiktionConfig.String.length(4)
                type<String>() using FiktionConfig.String.length(8)
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = AvoidMultipleConfigsForRuleTarget(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertNull(ktFile.modifiedText)
    }
}
