@file:OptIn(ExperimentalUuidApi::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UuidTest {
    @Test
    fun `uuid uses the current fake context seed`() {
        val fiktion =
            Fiktion {
                type<Uuid>() generatesBy { uuid() }
            }

        assertEquals(fiktion.fake<Uuid>(seed = 123), fiktion.fake<Uuid>(seed = 123))
    }
}
