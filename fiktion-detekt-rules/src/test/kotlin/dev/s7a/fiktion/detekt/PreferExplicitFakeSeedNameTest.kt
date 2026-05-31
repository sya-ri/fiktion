package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferExplicitFakeSeedNameTest {
    private val rule = PreferExplicitFakeSeedName(Config.empty)

    @Test
    fun `reports positional fake seeds`() {
        val findings =
            rule.lint(
                """
                fun test(fiktion: Fiktion) {
                    fake<User>(123)
                    fiktion.fake<User>(456) {
                        User::id generates "id"
                    }
                }
                """.trimIndent(),
            )

        assertEquals(2, findings.size)
        assertEquals("Use `seed = 123` instead of positional fake seed `123`.", findings[0].message)
        assertEquals("Use `seed = 456` instead of positional fake seed `456`.", findings[1].message)
    }

    @Test
    fun `does not report explicit seed names or ambiguous overloads`() {
        val findings =
            rule.lint(
                """
                fun test(type: KType) {
                    fake<User>(seed = 123)
                    fake<User>()
                    fake(type, 123)
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `auto corrects positional fake seeds`() {
        val code =
            """
            fun test() {
                fake<User>(123) {
                    User::id generates "id"
                }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferExplicitFakeSeedName(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertEquals(
            """
            fun test() {
                fake<User>(seed = 123) {
                    User::id generates "id"
                }
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
