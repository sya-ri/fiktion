package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Config.Companion.AUTO_CORRECT_KEY
import dev.detekt.api.modifiedText
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferContainerPartFakeHelpersTest {
    private val rule = PreferContainerPartFakeHelpers(Config.empty)

    @Test
    fun `reports raw fake calls that should use container part helpers`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    typeFamily<CustomList<*>>() generatesBy {
                        CustomList(List(3) { index -> fake(index, argumentIndex = 0) })
                    }
                    typeFamily<CustomMap<*, *>>() generatesBy {
                        CustomMap(List(3) { index -> fake(index, argumentIndex = 0) to fake(index, argumentIndex = 1) }.toMap())
                    }
                    this.typeFamily<CustomList<*>>() generatesBy {
                        CustomList(List(3) { index -> this.fake(index, argumentIndex = 0) })
                    }
                    typeFamily<CustomList<*>>().generatesBy {
                        CustomList(List(3) { index -> fake(index = index, argumentIndex = 0) })
                    }
                }
                """.trimIndent(),
            )

        assertEquals(5, findings.size)
        assertEquals(
            "Use `fakeElement(index)` instead of `fake(index, argumentIndex = 0)` so container target config applies.",
            findings[0].message,
        )
        assertEquals(
            "Use `fakeKey(index)` instead of `fake(index, argumentIndex = 0)` so container target config applies.",
            findings[1].message,
        )
        assertEquals(
            "Use `fakeValue(index)` instead of `fake(index, argumentIndex = 1)` so container target config applies.",
            findings[2].message,
        )
    }

    @Test
    fun `does not report non-container fake calls`() {
        val findings =
            rule.lint(
                """
                fun configure(index: Int) {
                    typeFamily<Box<*>>() generatesBy {
                        Box(fake(index, argumentIndex = 0))
                    }
                    typeFamily<CustomList<*>>() generatesBy {
                        CustomList(List(3) { index -> fakeElement(index) })
                    }
                    typeFamily<CustomMap<*, *>>() generatesBy {
                        CustomMap(List(3) { index -> fake(index, argumentIndex = 1) to fake(index, argumentIndex = 0) }.toMap())
                    }
                    type<CustomList<Int>>() generatesBy {
                        CustomList(List(3) { index -> fake(index, argumentIndex = 0) })
                    }
                    typeFamily<CustomList<*>>() generatesBy {
                        CustomList(List(3) { index -> fake(index) })
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `uses configured indexed loop calls`() {
        val findings =
            PreferContainerPartFakeHelpers(TestConfig("indexedLoopCalls" to listOf("buildList"))).lint(
                """
                fun configure() {
                    typeFamily<CustomList<*>>() generatesBy {
                        CustomList(List(3) { index -> fake(index, argumentIndex = 0) })
                    }
                    typeFamily<CustomList<*>>() generatesBy {
                        CustomList(buildList(3) { index -> fake(index, argumentIndex = 0) })
                    }
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals(
            "Use `fakeElement(index)` instead of `fake(index, argumentIndex = 0)` so container target config applies.",
            findings.single().message,
        )
    }

    @Test
    fun `auto corrects raw fake calls to container part helpers`() {
        val code =
            """
            fun configure() {
                typeFamily<CustomList<*>>() generatesBy {
                    CustomList(List(3) { index -> fake(index, argumentIndex = 0) })
                }
                typeFamily<CustomMap<*, *>>() generatesBy {
                    CustomMap(List(3) { index -> fake(index, argumentIndex = 0) to fake(index, argumentIndex = 1) }.toMap())
                }
                this.typeFamily<CustomList<*>>() generatesBy {
                    CustomList(List(3) { index -> this.fake(index, argumentIndex = 0) })
                }
                typeFamily<CustomList<*>>().generatesBy {
                    CustomList(List(3) { index -> fake(index = index, argumentIndex = 0) })
                }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = PreferContainerPartFakeHelpers(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(5, findings.size)
        assertEquals(
            """
            fun configure() {
                typeFamily<CustomList<*>>() generatesBy {
                    CustomList(List(3) { index -> fakeElement(index) })
                }
                typeFamily<CustomMap<*, *>>() generatesBy {
                    CustomMap(List(3) { index -> fakeKey(index) to fakeValue(index) }.toMap())
                }
                this.typeFamily<CustomList<*>>() generatesBy {
                    CustomList(List(3) { index -> this.fakeElement(index) })
                }
                typeFamily<CustomList<*>>().generatesBy {
                    CustomList(List(3) { index -> fakeElement(index) })
                }
            }
            """.trimIndent(),
            ktFile.modifiedText,
        )
    }
}
