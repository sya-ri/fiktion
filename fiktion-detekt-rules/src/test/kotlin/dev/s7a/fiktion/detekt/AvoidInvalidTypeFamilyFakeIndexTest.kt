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

class AvoidInvalidTypeFamilyFakeIndexTest {
    private val rule = AvoidInvalidTypeFamilyFakeIndex(Config.empty)

    @Test
    fun `reports fake calls outside type-family argument indexes`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    typeFamily<Box<*>>() generatesBy {
                        Box(fake(1))
                    }
                    typeFamily<Pair<*, *>>() generatesBy {
                        Pair(fake(0), fake(2))
                    }
                    typeFamily<Result<*>?>() generatesBy {
                        Result.success(fake(9))
                    }
                    this.typeFamily<Box<*>>() generatesBy {
                        Box(this.fake(1))
                    }
                    typeFamily<Box<*>>().generatesBy {
                        Box(fake(1))
                    }
                }
                """.trimIndent(),
            )

        assertEquals(5, findings.size)
        assertEquals(
            "`fake(1)` is outside the requested type arguments. Allowed indexes are 0..0.",
            findings[0].message,
        )
        assertEquals(
            "`fake(2)` is outside the requested type arguments. Allowed indexes are 0..1.",
            findings[1].message,
        )
        assertEquals(
            "`fake(9)` is outside the requested type arguments. Allowed indexes are 0..0.",
            findings[2].message,
        )
    }

    @Test
    fun `does not report valid fake argument indexes or unsupported targets`() {
        val findings =
            rule.lint(
                """
                typealias UserList = List<User>

                fun configure(index: Int) {
                    typeFamily<Box<*>>() generatesBy {
                        Box(fake(0))
                    }
                    typeFamily<Pair<*, *>>() generatesBy {
                        Pair(fake(0), fake(1))
                    }
                    typeFamily<Box<*>>() generatesBy {
                        Box(fake(index, argumentIndex = 0))
                    }
                    typeFamily<Box<*>>() generatesBy {
                        Box(fakeElement(1))
                    }
                    type<Box<Int>>() generatesBy {
                        Box(fake(1))
                    }
                    typeFamily<UserList>() generatesBy {
                        fake(0)
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `does not autocorrect invalid fake argument indexes`() {
        val code =
            """
            fun configure() {
                typeFamily<Box<*>>() generatesBy {
                    Box(fake(1))
                }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = AvoidInvalidTypeFamilyFakeIndex(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertNull(ktFile.modifiedText)
    }
}
