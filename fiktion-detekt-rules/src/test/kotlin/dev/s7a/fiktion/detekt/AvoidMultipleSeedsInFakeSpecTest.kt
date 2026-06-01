package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.test.lint
import kotlin.test.Test
import kotlin.test.assertEquals

class AvoidMultipleSeedsInFakeSpecTest {
    private val rule = AvoidMultipleSeedsInFakeSpec(Config.empty)

    @Test
    fun `reports repeated withSeed declarations in the same block`() {
        val findings =
            rule.lint(
                """
                fun test() {
                    fake<User> {
                        withSeed(1)
                        withSeed(2)
                    }
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals("Use only one `withSeed` declaration in the same Fiktion spec block.", findings[0].message)
    }

    @Test
    fun `does not report a single seed declaration`() {
        val findings =
            rule.lint(
                """
                fun test() {
                    fake<User> {
                        withSeed(1)
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `does not report repeated withSeed calls outside fake spec block`() {
        val findings =
            rule.lint(
                """
                fun helper() {
                    withSeed(1)
                    withSeed(2)
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }
}
