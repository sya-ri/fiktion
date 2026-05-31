package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AvoidRandomInstanceInGeneratorTest {
    @Test
    fun `FakeContext random is derived from the fake seed`() {
        val fiktion =
            Fiktion {
                type<Int>() generatesBy { random.nextInt() }
            }

        assertEquals(fiktion.fake<Int>(seed = 1), fiktion.fake<Int>(seed = 1))
        assertNotEquals(fiktion.fake<Int>(seed = 1), fiktion.fake<Int>(seed = 2))
    }

    @Test
    fun `Random instance replacement intentionally changes generation to use the fake seed`() {
        val randomInstance =
            Fiktion {
                type<Int>() generatesBy { Random(1).nextInt() }
            }
        val fakeContextRandom =
            Fiktion {
                type<Int>() generatesBy { random.nextInt() }
            }

        assertEquals(randomInstance.fake<Int>(seed = 1), randomInstance.fake<Int>(seed = 2))
        assertNotEquals(fakeContextRandom.fake<Int>(seed = 1), fakeContextRandom.fake<Int>(seed = 2))
    }
}
