@file:OptIn(kotlin.time.ExperimentalTime::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class InstantTest {
    @Test
    fun `instant uses the current fake context seed`() {
        val fiktion =
            Fiktion {
                type<Instant>() generatesBy { instant() }
            }

        assertEquals(fiktion.fake<Instant>(seed = 123), fiktion.fake<Instant>(seed = 123))
    }
}
