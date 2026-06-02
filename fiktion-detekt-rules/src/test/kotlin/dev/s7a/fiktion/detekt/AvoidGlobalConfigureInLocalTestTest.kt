package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.test.lint
import kotlin.test.Test
import kotlin.test.assertEquals

class AvoidGlobalConfigureInLocalTestTest {
    private val rule = AvoidGlobalConfigureInLocalTest(Config.empty)

    @Test
    fun `reports global configure inside test functions`() {
        val findings =
            rule.lint(
                """
                class UserTest {
                    @Test
                    fun createsUser() {
                        Fiktion.configure { }
                    }

                    fun testCreatesUser() {
                        Fiktion.configure { }
                    }
                }
                """.trimIndent(),
            )

        assertEquals(2, findings.size)
        assertEquals(
            "Avoid `Fiktion.configure` inside a local test function; prefer per-call `fake { ... }` or a scoped `Fiktion { ... }` instance.",
            findings[0].message,
        )
    }

    @Test
    fun `does not report configure outside test functions`() {
        val findings =
            rule.lint(
                """
                fun setupDefaults() {
                    Fiktion.configure { }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }
}
