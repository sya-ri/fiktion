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

class AvoidGlobalFiktionConfigureWithoutRestoreTest {
    private val rule = AvoidGlobalFiktionConfigureWithoutRestore(Config.empty)

    @Test
    fun `reports configure snapshot without restore in the same block`() {
        val findings =
            rule.lint(
                """
                fun test() {
                    val snapshot = Fiktion.configure {
                        type<String>() generates "value"
                    }

                    fake<String>()
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals("Restore the Fiktion.configure snapshot `snapshot` in the same block.", findings.single().message)
    }

    @Test
    fun `does not report configure snapshot restored in the same block`() {
        val findings =
            rule.lint(
                """
                fun test() {
                    val snapshot = Fiktion.configure {
                        type<String>() generates "value"
                    }

                    try {
                        fake<String>()
                    } finally {
                        snapshot.restore(force = true)
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `does not report discarded configure snapshots`() {
        val findings =
            rule.lint(
                """
                fun test() {
                    Fiktion.configure {
                        type<String>() generates "value"
                    }
                }
                """.trimIndent(),
            )

        assertEquals(0, findings.size)
    }

    @Test
    fun `does not autocorrect missing restore`() {
        val code =
            """
            fun test() {
                val snapshot = Fiktion.configure { }
            }
            """.trimIndent()
        val ktFile = compileContentForTest(code)
        val findings = AvoidGlobalFiktionConfigureWithoutRestore(TestConfig(AUTO_CORRECT_KEY to true)).lint(ktFile)

        assertEquals(1, findings.size)
        assertNull(ktFile.modifiedText)
    }
}
