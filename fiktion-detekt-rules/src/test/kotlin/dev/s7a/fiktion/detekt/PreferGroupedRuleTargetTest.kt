package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferGroupedRuleTargetTest {
    private val rule = PreferGroupedRuleTarget(Config.empty)

    @Test
    fun `reports adjacent rules for the same target`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<User>() using FiktionConfig.String.length(10)
                    type<User>() generatesBy { User(id = fake()) }

                    property(User::id) using FiktionConfig.String.length(8)
                    property(User::id) generates "user-id"

                    name<String>("email") using FiktionConfig.String.length(24)
                    name<String>("email") generatesBy { "user@example.test" }
                }
                """.trimIndent(),
            )

        assertEquals(3, findings.size)
        assertEquals(
            "Group rules for `type<User>()` into a single target block.",
            findings[0].message,
        )
        assertEquals(
            "Group rules for `property(User::id)` into a single target block.",
            findings[1].message,
        )
        assertEquals(
            "Group rules for `name<String>(\"email\")` into a single target block.",
            findings[2].message,
        )
    }

    @Test
    fun `does not report separated or unsupported targets`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<User>() generatesBy { User(id = fake()) }
                    other()
                    type<User>() using FiktionConfig.String.length(10)

                    type<User>() generatesBy { User(id = fake()) }
                    type<Account>() using FiktionConfig.String.length(10)

                    name("email") generates "user@example.test"
                    name("email") generatesBy { "user@example.test" }

                    User::id generates "user-id"
                    User::id using FiktionConfig.String.length(8)
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size, findings.joinToString { finding -> finding.message })
    }

    @Test
    fun `auto corrects adjacent rules to grouped target blocks`() {
        val code =
            """
            fun configure() {
                type<User>() using FiktionConfig.String.length(10)
                type<User>() generatesBy { User(id = fake()) }

                property(User::id) using FiktionConfig.String.length(8)
                property(User::id) generates "user-id"

                name<String>("email") using FiktionConfig.String.length(24)
                name<String>("email") generatesBy { "user@example.test" }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferGroupedRuleTarget(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(3, findings.size)
        assertEquals(
            """
            import dev.s7a.fiktion.invoke

            fun configure() {
                type<User>() {
                    this using FiktionConfig.String.length(10)
                    this generatesBy { User(id = fake()) }
                }

                property(User::id) {
                    this using FiktionConfig.String.length(8)
                    this generates "user-id"
                }

                name<String>("email").invoke {
                    this using FiktionConfig.String.length(24)
                    this generatesBy { "user@example.test" }
                }
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }

    @Test
    fun `does not add invoke import when it is already available`() {
        val code =
            """
            import dev.s7a.fiktion.*

            fun configure() {
                type<User>() using FiktionConfig.String.length(10)
                type<User>() generatesBy { User(id = fake()) }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferGroupedRuleTarget(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertEquals(
            """
            import dev.s7a.fiktion.*

            fun configure() {
                type<User>() {
                    this using FiktionConfig.String.length(10)
                    this generatesBy { User(id = fake()) }
                }
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
