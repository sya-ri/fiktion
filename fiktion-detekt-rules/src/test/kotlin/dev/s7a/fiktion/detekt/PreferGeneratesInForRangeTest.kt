package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferGeneratesInForRangeTest {
    @Test
    fun `reports range generators`() {
        val findings =
            PreferGeneratesInForRange(Config.empty).lint(
                """
                fun configure() {
                    type<Int>() generatesBy { int(1..10) }
                    type<Long>().generatesBy { long(1L..10L) }
                    User::letter generatesBy { char('a'..'z') }
                }
                """.trimIndent(),
            )

        assertEquals(3, findings.size)
        assertEquals("Use `type<Int>() generatesIn 1..10` instead of `type<Int>() generatesBy { int(1..10) }`.", findings[0].message)
    }

    @Test
    fun `auto corrects range generators`() {
        val ktFile =
            compileContentForTest(
                """
                fun configure() {
                    type<Int>() generatesBy { int(1..10) }
                }
                """.trimIndent(),
            )

        val findings = PreferGeneratesInForRange(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertEquals(
            """
            import dev.s7a.fiktion.generatesIn

            fun configure() {
                type<Int>() generatesIn 1..10
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }

    @Test
    fun `does not add import when there are no range replacements`() {
        val code =
            """
            fun configure() {
                type<Int>() generatesBy { value }
                type<Char>() generatesBy { char(FiktionCharset.Alphanumeric) }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferGeneratesInForRange(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(0, findings.size)
        assertEquals(null, ktFile.modifiedText)
    }
}
