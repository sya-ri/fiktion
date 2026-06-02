package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.modifiedText
import dev.detekt.test.lint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AvoidNonPropertyRuleTargetsTest {
    private val rule = AvoidNonPropertyRuleTargets(Config.empty)

    @Test
    fun `reports non property rule targets`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    Fiktion {
                        type<String>() generates "value"
                        type<String>().generates("value")
                        type<String>() using FiktionConfig.String.length(4)
                        typeFamily<List<*>>() generatesBy { fake(0) }
                        name<String>("id") generates "value"
                        name<String>("id").generates("value")
                        name<String>("id") using FiktionConfig.String.length(4)
                        type<String> {
                            this generates "value"
                        }
                        name<String>("id") {
                            this generates "value"
                        }
                    }
                }
                """.trimIndent(),
            )

        assertEquals(9, findings.size)
        assertEquals(
            "Use an explicit property target instead of `type<String>()`.",
            findings[0].message,
        )
        assertEquals(
            "Use an explicit property target instead of `typeFamily<List<*>>()`.",
            findings[3].message,
        )
        assertEquals(
            """Use an explicit property target instead of `name<String>("id")`.""",
            findings[4].message,
        )
    }

    @Test
    fun `reports non property rule targets in fake configuration`() {
        val findings =
            rule.lint(
                """
                fun build() {
                    fake<User> {
                        type<String>() generates "value"
                        name<String>("id") generates "value"
                    }
                }
                """.trimIndent(),
            )

        assertEquals(2, findings.size)
        assertEquals(
            "Use an explicit property target instead of `type<String>()`.",
            findings[0].message,
        )
    }

    @Test
    fun `does not report explicit property rule targets`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    Fiktion {
                        User::id generates "value"
                        User::id using FiktionConfig.String.length(4)
                        property(User::id) generates "value"
                        property<User, String>("id") generates "value"
                        property<User, String>(Regex("id")) generates "value"
                        (User::profile / Profile::nickname) generates "value"
                        property(User::profile / Profile::nickname) generates "value"
                        property<User, String>("id") {
                            this generates "value"
                            this using FiktionConfig.String.length(4)
                        }
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `does not report non property targets in global configuration scopes`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<String>() generates "value"
                    name<String>("id") generates "value"
                }

                fun global() {
                    Fiktion.configure {
                        type<String>() generates "value"
                        name<String>("id") generates "value"
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `does not autocorrect`() {
        val code =
            """
            fun configure() {
                Fiktion {
                    type<String>() generates "value"
                }
            }
            """.trimIndent()
        val findings = rule.lint(code)

        assertEquals(1, findings.size)
        assertNull(
            findings
                .first()
                .entity
                .ktElement
                .containingKtFile
                .modifiedText,
        )
    }
}
