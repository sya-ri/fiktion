package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.test.lint
import kotlin.test.Test
import kotlin.test.assertEquals

class AvoidContainerPartOnStarProjectedUnknownTest {
    private val rule = AvoidContainerPartOnStarProjectedUnknown(Config.empty)

    @Test
    fun `reports container part targets on star-projected targets`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<List<*>>().element generates 1
                    type<Map<*, *>>().key generates "key"
                    type<Map<*, *>>().value generates 1
                }
                """.trimIndent(),
            )

        assertEquals(3, findings.size)
        assertEquals(
            "Avoid `element` on a star-projected container target; use a target with concrete type arguments.",
            findings[0].message,
        )
    }

    @Test
    fun `does not report concrete container part targets`() {
        val findings =
            rule.lint(
                """
                fun configure() {
                    type<List<String>>().element generates "value"
                    type<Map<String, Int>>().key generates "key"
                    type<Map<String, Int>>().value generates 1
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }
}
