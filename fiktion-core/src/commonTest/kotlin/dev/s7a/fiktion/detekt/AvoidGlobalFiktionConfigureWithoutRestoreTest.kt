package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AvoidGlobalFiktionConfigureWithoutRestoreTest {
    @Test
    fun `restoring configure snapshot returns global fake calls to previous configuration`() {
        val baseline =
            Fiktion.configure {
                type<String>() generates "baseline"
            }
        try {
            val local =
                Fiktion.configure {
                    type<String>() generates "local"
                }

            assertEquals("local", fake<String>())
            assertTrue(local.restore(force = true))
            assertEquals("baseline", fake<String>())
        } finally {
            baseline.restore(force = true)
        }
    }
}
