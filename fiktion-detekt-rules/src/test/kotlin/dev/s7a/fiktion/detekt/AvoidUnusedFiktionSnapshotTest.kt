package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.test.lint
import kotlin.test.Test
import kotlin.test.assertEquals

class AvoidUnusedFiktionSnapshotTest {
    @Test
    fun `reports discarded configure snapshots`() {
        val findings =
            AvoidUnusedFiktionSnapshot(Config.empty).lint(
                """
                fun setup() {
                    Fiktion.configure { }
                    val snapshot = Fiktion.configure { }
                }
                """.trimIndent(),
            )

        assertEquals(1, findings.size)
        assertEquals("Store the Fiktion.configure snapshot and restore it after the test scope.", findings.single().message)
    }
}
