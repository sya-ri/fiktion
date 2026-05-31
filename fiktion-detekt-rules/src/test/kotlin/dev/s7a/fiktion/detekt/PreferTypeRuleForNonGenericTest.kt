package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferTypeRuleForNonGenericTest {
    private val rule = PreferTypeRuleForNonGeneric(Config.empty)

    @Test
    fun `reports non-generic type-family rules`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    typeFamily<Boolean>() generatesBy { true }
                    this.typeFamily<User>() generatesBy { User() }
                }
                """.trimIndent(),
            )

        assertEquals(2, findings.size)
        assertEquals("Use `type<Boolean>()` instead of `typeFamily<Boolean>()`.", findings[0].message)
        assertEquals("Use `type<User>()` instead of `typeFamily<User>()`.", findings[1].message)
    }

    @Test
    fun `does not report generic type-family rules`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    typeFamily<List<String>>() generatesBy { listOf("value") }
                    typeFamily<Box<*>>() generatesBy { Box(fake(0)) }
                    typeFamily<Map<String, Int>>() generatesBy { mapOf("key" to 1) }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `does not report type-family rules that use type argument fake helpers`() {
        val findings =
            rule.lint(
                """
                typealias UserList = List<User>

                fun configure() {
                    typeFamily<UserList>() generatesBy { fake(0) }
                    typeFamily<UserList>() generatesBy { this.fake(0) }
                    typeFamily<UserList>() generatesBy { fakeElement(0) }
                    typeFamily<UserMap>() generatesBy { fakeKey(0) to fakeValue(0) }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `auto corrects non-generic type-family rules to exact type rules`() {
        val code =
            """
            fun configure() {
                typeFamily<Boolean>() generatesBy { true }
                this.typeFamily<User>() generatesBy { User() }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferTypeRuleForNonGeneric(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(2, findings.size)
        assertEquals(
            """
            import dev.s7a.fiktion.type

            fun configure() {
                type<Boolean>() generatesBy { true }
                this.type<User>() generatesBy { User() }
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }

    @Test
    fun `does not add type import when it is already available`() {
        val code =
            """
            import dev.s7a.fiktion.*

            fun configure() {
                typeFamily<Boolean>() generatesBy { true }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferTypeRuleForNonGeneric(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertEquals(
            """
            import dev.s7a.fiktion.*

            fun configure() {
                type<Boolean>() generatesBy { true }
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
