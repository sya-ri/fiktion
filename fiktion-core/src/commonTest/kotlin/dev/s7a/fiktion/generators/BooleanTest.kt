package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class BooleanTest {
    @Test
    fun `boolean uses the current fake context seed`() {
        val fiktion =
            Fiktion {
                type<Boolean>() generatesBy {
                    boolean()
                }
            }

        assertEquals(fiktion.fake<Boolean>(seed = 123), fiktion.fake<Boolean>(seed = 123))
    }
}
