package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferContainerConfigureHelpersTest {
    @Test
    fun `reports container materialization type families`() {
        val findings =
            PreferContainerConfigureHelpers(Config.empty).lint(
                """
                fun configure() {
                    typeFamily<CustomList<*>>() generatesBy {
                        CustomList(List(2) { index -> fakeElement(index) })
                    }
                    typeFamily<CustomMap<*, *>>() generatesBy {
                        CustomMap(List(2) { index -> fakeKey(index) to fakeValue(index) })
                    }
                    typeFamily<Box<*>>() generatesBy {
                        Box(fake(0))
                    }
                }
                """.trimIndent(),
            )

        assertEquals(2, findings.size)
        assertEquals(
            "Use `configureCollection` instead of `typeFamily<CustomList<*>>() generatesBy` when only materializing generated container parts.",
            findings[0].message,
        )
        assertEquals(
            "Use `configureMap` instead of `typeFamily<CustomMap<*, *>>() generatesBy` when only materializing generated container parts.",
            findings[1].message,
        )
    }

    @Test
    fun `does not report container generators with extra logic`() {
        val findings =
            PreferContainerConfigureHelpers(Config.empty).lint(
                """
                fun configure() {
                    typeFamily<CustomList<*>>() generatesBy {
                        val elements = List(2) { index -> fakeElement(index) }
                        CustomList(elements.filterNotNull())
                    }
                    typeFamily<CustomMap<*, *>>() generatesBy {
                        require(size >= 0)
                        CustomMap(List(2) { index -> fakeKey(index) to fakeValue(index) }.toMap())
                    }
                    typeFamily<CustomList<*>>() generatesBy {
                        CustomList(List(2) { index -> fakeElement(index) }.filterNotNull())
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `uses configured container materialization calls`() {
        val findings =
            PreferContainerConfigureHelpers(
                TestConfig("containerMaterializationCalls" to listOf("CustomList", "makeElements")),
            ).lint(
                """
                fun configure() {
                    typeFamily<CustomList<*>>() generatesBy {
                        CustomList(makeElements(2) { index -> fakeElement(index) })
                    }
                    typeFamily<CustomMap<*, *>>() generatesBy {
                        CustomMap(List(2) { index -> fakeKey(index) to fakeValue(index) })
                    }
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals(
            "Use `configureCollection` instead of `typeFamily<CustomList<*>>() generatesBy` when only materializing generated container parts.",
            findings.single().message,
        )
    }
}
