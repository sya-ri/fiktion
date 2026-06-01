package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferNamedTypeFamilyFakeArgumentIndexTest {
    private val rule = PreferNamedTypeFamilyFakeArgumentIndex(Config.empty)

    @Test
    fun `reports positional type-family fake argument indexes`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    typeFamily<Box<*, *>>() generatesBy {
                        Box(fake(0, 1), fakeElement(0, 1), fakeKey(0, 0), fakeValue(0, 1))
                    }
                    this.typeFamily<Box<*, *>>() generatesBy {
                        Box(this.fake(0, 1), this.fakeValue(0, 1))
                    }
                }
                """.trimIndent(),
            )

        assertEquals(6, findings.size)
        assertEquals(
            "Use `argumentIndex = 1` instead of positional type-family fake argument index `1`.",
            findings[0].message,
        )
    }

    @Test
    fun `does not report named or unrelated fake arguments`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    typeFamily<Box<*, *>>() generatesBy {
                        Box(fake(0), fake(0, argumentIndex = 1), fakeElement(0, argumentIndex = 1))
                    }
                    type<User>() generatesBy {
                        fake<User>(123)
                    }
                    fake<User>(123)
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `auto corrects positional type-family fake argument indexes`() {
        val code =
            """
            fun configure() {
                typeFamily<Box<*, *>>() generatesBy {
                    Box(fake(0, 1), fakeElement(0, 1), fakeKey(0, 0), fakeValue(0, 1))
                }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferNamedTypeFamilyFakeArgumentIndex(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(4, findings.size)
        assertEquals(
            """
            fun configure() {
                typeFamily<Box<*, *>>() generatesBy {
                    Box(fake(0, argumentIndex = 1), fakeElement(0, argumentIndex = 1), fakeKey(0, argumentIndex = 0), fakeValue(0, argumentIndex = 1))
                }
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
