package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferPropertyShorthandTest {
    @Test
    fun `reports property call shorthand`() {
        val findings =
            PreferPropertyShorthand(Config.empty).lint(
                """
                fun configure() {
                    property(User::id) generates "id"
                    property(User::age) using FiktionConfig.Int.range(1..10)
                }
                """.trimIndent(),
            )

        assertEquals(2, findings.size)
        assertEquals("Use `User::id generates \"id\"` instead of `property(User::id) generates \"id\"`.", findings[0].message)
    }

    @Test
    fun `auto corrects property call shorthand`() {
        val ktFile =
            compileContentForTest(
                """
                fun configure() {
                    property(User::id) generates "id"
                }
                """.trimIndent(),
            )

        val findings = PreferPropertyShorthand(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertEquals(
            """
            fun configure() {
                User::id generates "id"
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
