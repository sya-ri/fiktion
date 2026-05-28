@file:OptIn(ExperimentalTime::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class DurationTest {
    @Test
    fun `duration uses the current fake context seed`() {
        val fiktion =
            Fiktion {
                type<Duration>() generatesBy { duration() }
            }

        assertEquals(fiktion.fake<Duration>(seed = 123), fiktion.fake<Duration>(seed = 123))
    }
}
