package dev.s7a.fiktion.generators

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.test.Test
import kotlin.test.assertEquals

class ByteArrayTest {
    @Test
    fun `byteArray generates configured size`() {
        val fiktion = Fiktion { type<ByteArray>() generatesBy { byteArray(size = 3) } }

        assertEquals(3, fiktion.fake<ByteArray>(seed = 1).size)
    }
}
