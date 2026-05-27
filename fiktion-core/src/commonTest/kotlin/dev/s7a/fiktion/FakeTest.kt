package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeTest {
    @Test
    fun `fake generates built-in primitive values without explicit rules`() {
        assertIs<String>(fake<String>())
        assertIs<Int>(fake<Int>())
        assertIs<Long>(fake<Long>())
        assertIs<Boolean>(fake<Boolean>())
        assertIs<Double>(fake<Double>())
    }

    @Test
    fun `fake uses deterministic generation when a seed is provided`() {
        assertEquals(fake<String>(seed = 123), fake<String>(seed = 123))
        assertEquals(fake<Int>(seed = 123), fake<Int>(seed = 123))
        assertEquals(fake<Long>(seed = 123), fake<Long>(seed = 123))
        assertEquals(fake<Boolean>(seed = 123), fake<Boolean>(seed = 123))
        assertEquals(fake<Double>(seed = 123), fake<Double>(seed = 123))
    }

    @Test
    fun `fake changes generated values when the seed changes`() {
        assertNotEquals(fake<String>(seed = 123), fake<String>(seed = 456))
        assertNotEquals(fake<Int>(seed = 123), fake<Int>(seed = 456))
        assertNotEquals(fake<Long>(seed = 123), fake<Long>(seed = 456))
        assertNotEquals(fake<Double>(seed = 123), fake<Double>(seed = 456))
    }

    @Test
    fun `per-call name rules can declare the value type explicitly`() {
        val spec = FakeSpec<User>()

        with(spec) {
            name<String>("id") generates "user-1"
        }

        assertEquals(RuleKey.Name("id", typeOf<String>()), spec.rules.single().key)
    }

    @Test
    fun `fake returns null for a nullable type when null probability always applies`() {
        val fiktion =
            Fiktion {
                type<String?>() generates "value" orNullAt 1.0
            }

        val value =
            fiktion.fake<String?>(seed = 1)

        assertNull(value)
    }
}
