package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferGeneratesForFixedValueTest {
    private val rule = PreferGeneratesForFixedValue(Config.empty)

    @Test
    fun `reports generator rules that can be fixed values`() {
        val findings =
            rule.lint(
                $$$"""
                fun configure() {
                    type<Int>() generatesBy { 42 }
                    type<Int>() generatesBy { -42 }
                    type<String>() generatesBy { "value" }
                    type<String>() generatesBy { $$"$xxx" }
                    type<String?>() generatesBy { null }
                    type<Boolean>() generatesBy { true }
                    type<Int>() generatesBy { (42) }
                    User::id generatesBy { "user-1" }
                    fiktion.type<Int>() generatesBy { 42 }
                    type<Int>() generatesBy { oneOf(1) }
                    type<Int>() generatesOneOf 1
                    type<Int>().generatesOneOf(1)
                    type<Int>() generatesOneOf listOf(1)
                    type<Int>().generatesOneOf(listOf(1))
                    type<Int>() generatesOneOf setOf(1)
                    type<Int>() generatesBy { oneOf(-1) }
                    type<String>() generatesBy { oneOf("one") }
                    type<String>() generatesBy { oneOf($$"$yyy") }
                    type<String?>() generatesBy { oneOf(null) }
                    type<Boolean>() generatesBy { oneOf(true) }
                    type<Int>() generatesBy { oneOf((1)) }
                    User::name generatesBy { oneOf("user-2") }
                }
                """.trimIndent(),
            )

        assertEquals(22, findings.size)
        assertEquals(
            "Use `type<Int>() generates 42` instead of `type<Int>() generatesBy { 42 }`.",
            findings[0].message,
        )
        assertEquals(
            "Use `type<Int>() generates -42` instead of `type<Int>() generatesBy { -42 }`.",
            findings[1].message,
        )
        assertEquals(
            "Use `type<String>() generates \"value\"` instead of `type<String>() generatesBy { \"value\" }`.",
            findings[2].message,
        )
        assertEquals(
            $$$"""Use `type<String>() generates $$"$xxx"` instead of `type<String>() generatesBy { $$"$xxx" }`.""",
            findings[3].message,
        )
        assertEquals(
            "Use `type<String?>() generates null` instead of `type<String?>() generatesBy { null }`.",
            findings[4].message,
        )
        assertEquals(
            "Use `type<Int>() generates 1` instead of `type<Int>() generatesBy { oneOf(1) }`.",
            findings[9].message,
        )
        assertEquals(
            "Use `type<Int>() generates 1` instead of `type<Int>() generatesOneOf listOf(1)`.",
            findings[12].message,
        )
    }

    @Test
    fun `does not report non fixed generator values`() {
        val findings =
            rule.lint(
                $$"""
                fun configure(value: Int) {
                    type<Int>() generatesBy { index }
                    typeFamily<Boolean>() generatesBy { true }
                    this.typeFamily<Int>() generatesBy { 42 }
                    typeFamily<List<*>>() generatesBy { null }
                    customTarget<Boolean>() generatesBy { true }
                    type<String>() generatesBy { "value-$seed" }
                    type<String>() generatesBy { value }
                    type<Int>() generatesBy {
                        val generated = 42
                        generated
                    }
                    type<Int>() generatesBy { oneOf(value) }
                    type<Int>() generatesBy { oneOf(createValue()) }
                    type<Int>() generatesBy { oneOf(1, 2) }
                    type<List<Int>>() generatesBy { oneOf(listOf(1)) }
                    type<Set<Int>>() generatesBy { oneOf(setOf(1)) }
                    type<Int>() generatesOneOf listOf(value)
                    type<Int>().generatesOneOf(listOf(value))
                    type<Int>() generatesOneOf createValues()
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `auto corrects generator rules to fixed values`() {
        val code =
            $$$"""
            fun configure() {
                type<Int>() generatesBy { 42 }
                type<String>() generatesBy { "value" }
                type<String>() generatesBy { $$"$xxx" }
                User::id generatesBy { "user-1" }
                fiktion.type<Int>() generatesBy { 42 }
                type<Int>() generatesBy { oneOf(1) }
                type<Int>() generatesOneOf 1
                type<Int>().generatesOneOf(1)
                type<Int>() generatesOneOf listOf(1)
                type<Int>().generatesOneOf(listOf(1))
                type<Int>() generatesOneOf setOf(1)
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)

        val findings = PreferGeneratesForFixedValue(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(11, findings.size)
        assertEquals(
            $$$"""
            import dev.s7a.fiktion.generates

            fun configure() {
                type<Int>() generates 42
                type<String>() generates "value"
                type<String>() generates $$"$xxx"
                User::id generates "user-1"
                fiktion.type<Int>() generates 42
                type<Int>() generates 1
                type<Int>() generates 1
                type<Int>() generates 1
                type<Int>() generates 1
                type<Int>() generates 1
                type<Int>() generates 1
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }

    @Test
    fun `does not add generates import when it is already available`() {
        val code =
            """
            import dev.s7a.fiktion.*

            fun configure() {
                type<Int>() generatesBy { 42 }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferGeneratesForFixedValue(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertEquals(
            """
            import dev.s7a.fiktion.*

            fun configure() {
                type<Int>() generates 42
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }

    @Test
    fun `respects disabled fixed value sources`() {
        val findings =
            PreferGeneratesForFixedValue(
                TestConfig(
                    "constantGenerator" to false,
                    "singleOneOf" to false,
                    "singleGeneratesOneOf" to false,
                ),
            ).lint(
                """
                fun configure() {
                    type<Int>() generatesBy { 42 }
                    type<Int>() generatesBy { oneOf(1) }
                    type<Int>() generatesOneOf listOf(1)
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `respects disabled null fixed values`() {
        val findings =
            PreferGeneratesForFixedValue(TestConfig("includeNull" to false)).lint(
                """
                fun configure() {
                    type<String?>() generatesBy { null }
                    type<String?>() generatesBy { oneOf(null) }
                    type<String?>() generatesOneOf listOf(null)
                    type<Int>() generatesBy { 42 }
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals(
            "Use `type<Int>() generates 42` instead of `type<Int>() generatesBy { 42 }`.",
            findings[0].message,
        )
    }
}
