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

class AvoidRuleDeclarationsInLoopsTest {
    private val rule = AvoidRuleDeclarationsInLoops(Config.empty)

    @Test
    fun `reports rule declarations inside language loops`() {
        val findings =
            rule.lint(
                """
                fun configure(values: List<Int>) {
                    for (value in values) {
                        type<Int>() generates value
                    }

                    while (condition()) {
                        type<String>() using FiktionConfig.String.length(8)
                    }

                    do {
                        User::id generates "id"
                    } while (condition())
                }
                """.trimIndent(),
            )

        assertEquals(3, findings.size)
        assertEquals(
            "Avoid declaring Fiktion rules for `type<Int>()` inside loops; use one rule with generator logic instead.",
            findings[0].message,
        )
        assertEquals(
            "Avoid declaring Fiktion rules for `type<String>()` inside loops; use one rule with generator logic instead.",
            findings[1].message,
        )
        assertEquals(
            "Avoid declaring Fiktion rules for `User::id` inside loops; use one rule with generator logic instead.",
            findings[2].message,
        )
    }

    @Test
    fun `reports rule declarations inside loop call lambdas`() {
        val findings =
            rule.lint(
                """
                fun configure(values: List<Int>) {
                    repeat(3) { index ->
                        type<Int>() generates index
                    }

                    values.forEach { value ->
                        property(User::age).generates(value)
                    }

                    values.map { value ->
                        name<Int>("age") using FiktionConfig.Int.range(value..value)
                    }

                    values.forEachIndexed { index, value ->
                        this.type<Int>() generates value + index
                    }
                }
                """.trimIndent(),
            )

        assertEquals(4, findings.size)
        assertEquals(
            "Avoid declaring Fiktion rules for `type<Int>()` inside loops; use one rule with generator logic instead.",
            findings[0].message,
        )
        assertEquals(
            "Avoid declaring Fiktion rules for `property(User::age)` inside loops; use one rule with generator logic instead.",
            findings[1].message,
        )
        assertEquals(
            "Avoid declaring Fiktion rules for `name<Int>(\"age\")` inside loops; use one rule with generator logic instead.",
            findings[2].message,
        )
        assertEquals(
            "Avoid declaring Fiktion rules for `this.type<Int>()` inside loops; use one rule with generator logic instead.",
            findings[3].message,
        )
    }

    @Test
    fun `reports rules inside grouped target blocks in loops`() {
        val findings =
            rule.lint(
                """
                fun configure(values: List<Int>) {
                    values.forEach { value ->
                        type<Int>() {
                            this using FiktionConfig.Int.range(value..value)
                            this generates value
                        }
                    }
                }
                """.trimIndent(),
            )

        assertEquals(2, findings.size)
        assertEquals(
            "Avoid declaring Fiktion rules for `this` inside loops; use one rule with generator logic instead.",
            findings[0].message,
        )
        assertEquals(
            "Avoid declaring Fiktion rules for `this` inside loops; use one rule with generator logic instead.",
            findings[1].message,
        )
    }

    @Test
    fun `does not report rule declarations outside loops or non-rule calls inside loops`() {
        val findings =
            rule.lint(
                """
                fun configure(values: List<Int>) {
                    type<Int>() generatesBy { index }
                    type<String>() using FiktionConfig.String.length(8)

                    values.forEach { value ->
                        println(value)
                        fake<Int>()
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `uses configured loop calls`() {
        val findings =
            AvoidRuleDeclarationsInLoops(TestConfig("loopCalls" to listOf("customLoop"))).lint(
                """
                fun configure(values: List<Int>) {
                    values.forEach { value ->
                        type<Int>() generates value
                    }

                    customLoop {
                        type<String>() generates "value"
                    }
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals(
            "Avoid declaring Fiktion rules for `type<String>()` inside loops; use one rule with generator logic instead.",
            findings.single().message,
        )
    }

    @Test
    fun `does not autocorrect rule declarations inside loops`() {
        val code =
            """
            fun configure() {
                repeat(3) { index ->
                    type<Int>() generates index
                }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = AvoidRuleDeclarationsInLoops(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertNull(ktFile.modifiedText)
    }
}
