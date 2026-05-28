package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class UnitTest {
    @Test
    fun `unit generates Unit`() {
        val fiktion =
            Fiktion {
                type<Unit>() generatesBy { unit() }
            }

        assertEquals(Unit, fiktion.fake<Unit>())
    }
}
