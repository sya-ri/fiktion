package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferGeneratesByForMutableValuesTest {
    private val rule = PreferGeneratesByForMutableValues(Config.empty)

    @Test
    fun `reports mutable factory values`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<MutableList<String>>() generates mutableListOf("value")
                    type<MutableSet<String>>() generates mutableSetOf("value")
                    type<MutableMap<String, Int>>() generates mutableMapOf("key" to 1)
                    User::tags generates mutableListOf("value")
                    fiktion.type<MutableList<String>>() generates mutableListOf("value")
                    type<MutableList<String>>().generates(mutableListOf("value"))
                }
                """.trimIndent(),
            )

        assertEquals(6, findings.size)
        assertEquals(
            "Use `type<MutableList<String>>() generatesBy { mutableListOf(\"value\") }` " +
                "instead of `type<MutableList<String>>() generates mutableListOf(\"value\")`.",
            findings[0].message,
        )
        assertEquals(
            "Use `type<MutableSet<String>>() generatesBy { mutableSetOf(\"value\") }` " +
                "instead of `type<MutableSet<String>>() generates mutableSetOf(\"value\")`.",
            findings[1].message,
        )
        assertEquals(
            "Use `type<MutableMap<String, Int>>() generatesBy { mutableMapOf(\"key\" to 1) }` " +
                "instead of `type<MutableMap<String, Int>>() generates mutableMapOf(\"key\" to 1)`.",
            findings[2].message,
        )
        assertEquals(
            "Use `User::tags generatesBy { mutableListOf(\"value\") }` " +
                "instead of `User::tags generates mutableListOf(\"value\")`.",
            findings[3].message,
        )
        assertEquals(
            "Use `fiktion.type<MutableList<String>>() generatesBy { mutableListOf(\"value\") }` " +
                "instead of `fiktion.type<MutableList<String>>() generates mutableListOf(\"value\")`.",
            findings[4].message,
        )
        assertEquals(
            "Use `type<MutableList<String>>() generatesBy { mutableListOf(\"value\") }` " +
                "instead of `type<MutableList<String>>().generates(mutableListOf(\"value\"))`.",
            findings[5].message,
        )
    }

    @Test
    fun `does not report non mutable factory values`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<List<String>>() generates listOf("value")
                    type<Set<String>>() generates setOf("value")
                    type<Map<String, Int>>() generates mapOf("key" to 1)
                    type<MutableList<String>>() generates value
                    type<MutableList<String>>() generatesBy { mutableListOf("value") }
                    type<MutableList<String>>().generatesBy { mutableListOf("value") }
                    type<MutableList<String>>().generates(value)
                    typeFamily<MutableList<*>>() generates mutableListOf("value")
                    customTarget<MutableList<String>>() generates mutableListOf("value")
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `uses configured mutable factory calls`() {
        val findings =
            PreferGeneratesByForMutableValues(TestConfig("mutableFactoryCalls" to listOf("persistentListOf"))).lint(
                """
                fun configure() {
                    type<List<String>>() generates mutableListOf("value")
                    type<List<String>>() generates persistentListOf("value")
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals(
            "Use `type<List<String>>() generatesBy { persistentListOf(\"value\") }` " +
                "instead of `type<List<String>>() generates persistentListOf(\"value\")`.",
            findings.single().message,
        )
    }

    @Test
    fun `auto corrects mutable factory values to generator lambdas`() {
        val code =
            """
            fun configure() {
                type<MutableList<String>>() generates mutableListOf("value")
                type<MutableSet<String>>() generates mutableSetOf("value")
                type<MutableMap<String, Int>>() generates mutableMapOf("key" to 1)
                User::tags generates mutableListOf("value")
                fiktion.type<MutableList<String>>() generates mutableListOf("value")
                type<MutableList<String>>().generates(mutableListOf("value"))
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferGeneratesByForMutableValues(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(6, findings.size)
        assertEquals(
            """
            import dev.s7a.fiktion.generatesBy

            fun configure() {
                type<MutableList<String>>() generatesBy { mutableListOf("value") }
                type<MutableSet<String>>() generatesBy { mutableSetOf("value") }
                type<MutableMap<String, Int>>() generatesBy { mutableMapOf("key" to 1) }
                User::tags generatesBy { mutableListOf("value") }
                fiktion.type<MutableList<String>>() generatesBy { mutableListOf("value") }
                type<MutableList<String>>() generatesBy { mutableListOf("value") }
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }

    @Test
    fun `does not add generatesBy import when it is already available`() {
        val code =
            """
            import dev.s7a.fiktion.*

            fun configure() {
                type<MutableList<String>>() generates mutableListOf("value")
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferGeneratesByForMutableValues(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertEquals(
            """
            import dev.s7a.fiktion.*

            fun configure() {
                type<MutableList<String>>() generatesBy { mutableListOf("value") }
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
