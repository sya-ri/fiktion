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

class AvoidRecursiveFakeInGeneratorTest {
    private val rule = AvoidRecursiveFakeInGenerator(Config.empty)

    @Test
    fun `reports same-type fake calls inside type generators`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<User>() generatesBy {
                        fake<User>()
                    }
                    this.type<User>() generatesBy {
                        User(child = run { fake<User>() })
                    }
                    type<User>().generatesBy {
                        fake<User>()
                    }
                }
                """.trimIndent(),
            )

        assertEquals(3, findings.size)
        assertEquals(
            "Avoid recursive `fake<User>()` here. Add an explicit stopping condition or generate a smaller nested value.",
            findings[0].message,
        )
    }

    @Test
    fun `does not report different type or non-type generators`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<User>() generatesBy {
                        fake<Profile>()
                    }
                    typeFamily<Box<*>>() generatesBy {
                        fake<Box<Int>>()
                    }
                    type<User>() generatesBy {
                        if (depth > 2) User(children = emptyList()) else User(children = listOf(fake<Profile>()))
                    }
                    type<User>() generatesBy {
                        fake()
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `does not autocorrect recursive fake calls`() {
        val code =
            """
            fun configure() {
                type<User>() generatesBy {
                    fake<User>()
                }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = AvoidRecursiveFakeInGenerator(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertNull(ktFile.modifiedText)
    }
}
