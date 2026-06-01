package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferIndexedFakeInTypeFamilyLoopTest {
    private val rule = PreferIndexedFakeInTypeFamilyLoop(Config.empty)

    @Test
    fun `reports repeated fake calls in type-family loops`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    typeFamily<Box<*>>() generatesBy {
                        Box(List(3) { index -> fake(0) })
                    }
                    typeFamily<Box<*>>() generatesBy {
                        Box(List(3) { index -> run { fake(0) } })
                    }
                    this.typeFamily<Box<*>>() generatesBy {
                        Box(List(3) { this.fake(0) })
                    }
                    typeFamily<Box<*>>().generatesBy {
                        Box(List(3) { index -> fake(0) })
                    }
                }
                """.trimIndent(),
            )

        assertEquals(4, findings.size)
        assertEquals(
            "Use `fake(index, argumentIndex = 0)` instead of `fake(0)` to vary fake index in the loop.",
            findings[0].message,
        )
        assertEquals(
            "Use `fake(index, argumentIndex = 0)` instead of `fake(0)` to vary fake index in the loop.",
            findings[1].message,
        )
        assertEquals(
            "Use `fake(it, argumentIndex = 0)` instead of `fake(0)` to vary fake index in the loop.",
            findings[2].message,
        )
        assertEquals(
            "Use `fake(index, argumentIndex = 0)` instead of `fake(0)` to vary fake index in the loop.",
            findings[3].message,
        )
    }

    @Test
    fun `does not report fake calls outside type-family loops`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<Box<Int>>() generatesBy {
                        Box(List(3) { index -> fake(0) })
                    }
                    typeFamily<Box<*>>() generatesBy {
                        Box(fake(0))
                    }
                    typeFamily<Box<*>>() generatesBy {
                        Box(List(3) { index -> fake(index, argumentIndex = 0) })
                    }
                    typeFamily<Box<*>>() generatesBy {
                        Box(List(3) { index -> fakeElement(0) })
                    }
                    typeFamily<Box<*>>() generatesBy {
                        Box(List(3) { first, second -> fake(0) })
                    }
                    typeFamily<Box<*>>() generatesBy {
                        Box(run { fake(0) })
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `uses configured indexed loop calls`() {
        val findings =
            PreferIndexedFakeInTypeFamilyLoop(TestConfig("indexedLoopCalls" to listOf("buildList"))).lint(
                """
                fun configure() {
                    typeFamily<Box<*>>() generatesBy {
                        Box(List(3) { index -> fake(0) })
                    }
                    typeFamily<Box<*>>() generatesBy {
                        Box(buildList(3) { index -> fake(0) })
                    }
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals(
            "Use `fake(index, argumentIndex = 0)` instead of `fake(0)` to vary fake index in the loop.",
            findings.single().message,
        )
    }

    @Test
    fun `auto corrects repeated fake calls to indexed fake calls`() {
        val code =
            """
            fun configure() {
                typeFamily<Box<*>>() generatesBy {
                    Box(List(3) { index -> fake(0) })
                }
                typeFamily<Box<*>>() generatesBy {
                    Box(List(3) { index -> run { fake(0) } })
                }
                this.typeFamily<Box<*>>() generatesBy {
                    Box(List(3) { this.fake(0) })
                }
                typeFamily<Box<*>>().generatesBy {
                    Box(List(3) { index -> fake(0) })
                }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferIndexedFakeInTypeFamilyLoop(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(4, findings.size)
        assertEquals(
            """
            fun configure() {
                typeFamily<Box<*>>() generatesBy {
                    Box(List(3) { index -> fake(index, argumentIndex = 0) })
                }
                typeFamily<Box<*>>() generatesBy {
                    Box(List(3) { index -> run { fake(index, argumentIndex = 0) } })
                }
                this.typeFamily<Box<*>>() generatesBy {
                    Box(List(3) { this.fake(it, argumentIndex = 0) })
                }
                typeFamily<Box<*>>().generatesBy {
                    Box(List(3) { index -> fake(index, argumentIndex = 0) })
                }
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
