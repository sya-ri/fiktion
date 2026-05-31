package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferGeneratesOneOfTest {
    @Test
    fun `reports oneOf generators`() {
        val findings =
            PreferGeneratesOneOf(Config.empty).lint(
                """
                fun configure() {
                    type<Int>() generatesBy { oneOf(1, 2) }
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals(
            "Use `type<Int>() generatesOneOf listOf(1, 2)` instead of `type<Int>() generatesBy { oneOf(1, 2) }`.",
            findings[0].message,
        )
    }

    @Test
    fun `auto corrects oneOf generators`() {
        val ktFile =
            compileContentForTest(
                """
                fun configure() {
                    type<Int>() generatesBy { oneOf(1, 2) }
                }
                """.trimIndent(),
            )

        val findings = PreferGeneratesOneOf(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertEquals(
            """
            import dev.s7a.fiktion.generatesOneOf

            fun configure() {
                type<Int>() generatesOneOf listOf(1, 2)
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }

    @Test
    fun `does not report ambiguous single oneOf arguments`() {
        val findings =
            PreferGeneratesOneOf(Config.empty).lint(
                """
                fun configure(values: List<Int>) {
                    type<Int>() generatesBy { oneOf(1) }
                    type<Int>() generatesBy { oneOf(values) }
                    type<Int>() generatesBy { oneOf(createValues()) }
                    type<Int>() generatesBy { oneOf(listOf(1)) }
                    type<Int>() generatesBy { oneOf(setOf(1)) }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `does not add import when there are no oneOf replacements`() {
        val code =
            """
            fun configure() {
                type<Int>() generatesBy { value }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferGeneratesOneOf(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(0, findings.size)
        assertEquals(null, ktFile.modifiedText)
    }
}
