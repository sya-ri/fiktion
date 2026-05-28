package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.DurationUnit

class DurationUnitTest {
    @Test
    fun `durationUnit uses the current fake context seed`() {
        val fiktion =
            Fiktion {
                type<DurationUnit>() generatesBy { durationUnit() }
            }

        assertEquals(fiktion.fake<DurationUnit>(seed = 123), fiktion.fake<DurationUnit>(seed = 123))
    }
}
