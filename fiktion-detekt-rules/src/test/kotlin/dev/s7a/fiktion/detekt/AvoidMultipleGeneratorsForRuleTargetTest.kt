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

class AvoidMultipleGeneratorsForRuleTargetTest {
    private val rule = AvoidMultipleGeneratorsForRuleTarget(Config.empty)

    @Test
    fun `reports multiple generators for the same target in the same scope`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<Int>() generates 1
                    type<Int>() generatesBy { 2 }

                    type<Long>() generatesIn 0L..10L
                    type<Long>() generatesOneOf listOf(1L, 2L)

                    property(User::id) generates "first"
                    property(User::id).generates("second")

                    User::name generates "first"
                    User::name generatesBy { "second" }

                    name<String>("email") generates "first@example.test"
                    name<String>("email") generatesBy { "second@example.test" }

                    this.type<Boolean>() generates false
                    this.type<Boolean>() generates auto

                    typeFamily<Box<*>>() generatesBy { Box(fake(0)) }
                    typeFamily<Box<*>>() generatesBy { Box(fake(0)) }
                }
                """.trimIndent(),
            )

        assertEquals(7, findings.size)
        assertEquals("Avoid multiple generators for `type<Int>()` in the same scope.", findings[0].message)
        assertEquals("Avoid multiple generators for `type<Long>()` in the same scope.", findings[1].message)
        assertEquals("Avoid multiple generators for `property(User::id)` in the same scope.", findings[2].message)
        assertEquals("Avoid multiple generators for `User::name` in the same scope.", findings[3].message)
        assertEquals("Avoid multiple generators for `name<String>(\"email\")` in the same scope.", findings[4].message)
        assertEquals("Avoid multiple generators for `this.type<Boolean>()` in the same scope.", findings[5].message)
        assertEquals("Avoid multiple generators for `typeFamily<Box<*>>()` in the same scope.", findings[6].message)
    }

    @Test
    fun `reports multiple generators inside grouped target blocks`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<Int>() {
                        this generates 1
                        this generatesBy { 2 }
                    }
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals("Avoid multiple generators for `this` in the same scope.", findings.single().message)
    }

    @Test
    fun `reports multiple generators inside Fiktion blocks`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    Fiktion {
                        type<Int>() generates 1
                        type<Int>() generates 2
                    }

                    Fiktion.configure {
                        type<String>() generates "first"
                        type<String>() generates "second"
                    }
                }
                """.trimIndent(),
            )

        assertEquals(2, findings.size)
        assertEquals("Avoid multiple generators for `type<Int>()` in the same scope.", findings[0].message)
        assertEquals("Avoid multiple generators for `type<String>()` in the same scope.", findings[1].message)
    }

    @Test
    fun `does not report different targets or non-generator declarations`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<Int>() generates 1
                    type<String>() generates "value"
                    type<Int>() using FiktionConfig.Int.range(0..10)

                    name("email") generates "first@example.test"
                    name("email") generates "second@example.test"

                    type<Int>() {
                        this generates 1
                    }
                    type<Int>() {
                        this generates 2
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `does not autocorrect multiple generators`() {
        val code =
            """
            fun configure() {
                type<Int>() generates 1
                type<Int>() generates 2
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = AvoidMultipleGeneratorsForRuleTarget(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertNull(ktFile.modifiedText)
    }
}
