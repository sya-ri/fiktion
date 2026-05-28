@file:OptIn(kotlin.time.ExperimentalTime::class, kotlin.uuid.ExperimentalUuidApi::class)

package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.Instant
import kotlin.uuid.Uuid

class FakeTest {
    @Test
    fun `fake generates built-in Kotlin values without configuration`() {
        assertIs<String>(fake<String>())
        assertIs<Unit>(fake<Unit>())
        assertIs<Byte>(fake<Byte>())
        assertIs<Short>(fake<Short>())
        assertIs<Int>(fake<Int>())
        assertIs<Long>(fake<Long>())
        assertIs<Float>(fake<Float>())
        assertIs<Double>(fake<Double>())
        assertIs<Boolean>(fake<Boolean>())
        assertIs<Char>(fake<Char>())
        assertIs<IntRange>(fake<IntRange>())
        assertIs<LongRange>(fake<LongRange>())
        assertIs<CharRange>(fake<CharRange>())
        assertIs<UIntRange>(fake<UIntRange>())
        assertIs<ULongRange>(fake<ULongRange>())
        assertIs<IntProgression>(fake<IntProgression>())
        assertIs<LongProgression>(fake<LongProgression>())
        assertIs<CharProgression>(fake<CharProgression>())
        assertIs<UIntProgression>(fake<UIntProgression>())
        assertIs<ULongProgression>(fake<ULongProgression>())
        assertIs<Regex>(fake<Regex>())
        assertIs<Duration>(fake<Duration>())
        assertIs<DurationUnit>(fake<DurationUnit>())
        assertIs<Instant>(fake<Instant>())
        assertIs<UByte>(fake<UByte>())
        assertIs<UShort>(fake<UShort>())
        assertIs<UInt>(fake<UInt>())
        assertIs<ULong>(fake<ULong>())
        assertIs<Uuid>(fake<Uuid>())
        assertEquals(Unit, fake<Unit>())
        assertIs<String>(fake<Pair<String, Int>>().first)
        assertIs<Int>(fake<Pair<String, Int>>().second)
        assertIs<String>(fake<Triple<String, Int, Boolean>>().first)
        assertIs<Int>(fake<Triple<String, Int, Boolean>>().second)
        assertIs<Boolean>(fake<Triple<String, Int, Boolean>>().third)
        val result = fake<Result<String>>()
        assertTrue(result.isSuccess || result.isFailure)
    }

    @Test
    fun `fake generates nullable built-in Kotlin values without configuration`() {
        assertIs<String>(fake<String?>())
        assertIs<Unit>(fake<Unit?>())
        assertIs<Byte>(fake<Byte?>())
        assertIs<Short>(fake<Short?>())
        assertIs<Int>(fake<Int?>())
        assertIs<Long>(fake<Long?>())
        assertIs<Float>(fake<Float?>())
        assertIs<Double>(fake<Double?>())
        assertIs<Boolean>(fake<Boolean?>())
        assertIs<Char>(fake<Char?>())
        assertIs<IntRange>(fake<IntRange?>())
        assertIs<LongRange>(fake<LongRange?>())
        assertIs<CharRange>(fake<CharRange?>())
        assertIs<UIntRange>(fake<UIntRange?>())
        assertIs<ULongRange>(fake<ULongRange?>())
        assertIs<IntProgression>(fake<IntProgression?>())
        assertIs<LongProgression>(fake<LongProgression?>())
        assertIs<CharProgression>(fake<CharProgression?>())
        assertIs<UIntProgression>(fake<UIntProgression?>())
        assertIs<ULongProgression>(fake<ULongProgression?>())
        assertIs<Regex>(fake<Regex?>())
        assertIs<Duration>(fake<Duration?>())
        assertIs<DurationUnit>(fake<DurationUnit?>())
        assertIs<Instant>(fake<Instant?>())
        assertIs<UByte>(fake<UByte?>())
        assertIs<UShort>(fake<UShort?>())
        assertIs<UInt>(fake<UInt?>())
        assertIs<ULong>(fake<ULong?>())
        assertIs<Uuid>(fake<Uuid?>())
        assertIs<String>(fake<Pair<String, Int>?>()?.first)
        assertIs<String>(fake<Triple<String, Int, Boolean>?>()?.first)
        assertTrue(fake<Result<String>?>()?.let { it.isSuccess || it.isFailure } == true)
    }

    @Test
    fun `fake uses deterministic generation when a seed is provided`() {
        assertEquals(fake<String>(seed = 123), fake<String>(seed = 123))
        assertEquals(fake<Unit>(seed = 123), fake<Unit>(seed = 123))
        assertEquals(fake<Byte>(seed = 123), fake<Byte>(seed = 123))
        assertEquals(fake<Short>(seed = 123), fake<Short>(seed = 123))
        assertEquals(fake<Int>(seed = 123), fake<Int>(seed = 123))
        assertEquals(fake<Long>(seed = 123), fake<Long>(seed = 123))
        assertEquals(fake<Float>(seed = 123), fake<Float>(seed = 123))
        assertEquals(fake<Double>(seed = 123), fake<Double>(seed = 123))
        assertEquals(fake<Boolean>(seed = 123), fake<Boolean>(seed = 123))
        assertEquals(fake<Char>(seed = 123), fake<Char>(seed = 123))
        assertEquals(fake<IntRange>(seed = 123), fake<IntRange>(seed = 123))
        assertEquals(fake<LongRange>(seed = 123), fake<LongRange>(seed = 123))
        assertEquals(fake<CharRange>(seed = 123), fake<CharRange>(seed = 123))
        assertEquals(fake<UIntRange>(seed = 123), fake<UIntRange>(seed = 123))
        assertEquals(fake<ULongRange>(seed = 123), fake<ULongRange>(seed = 123))
        assertEquals(fake<IntProgression>(seed = 123).toList(), fake<IntProgression>(seed = 123).toList())
        assertEquals(fake<LongProgression>(seed = 123).toList(), fake<LongProgression>(seed = 123).toList())
        assertEquals(fake<CharProgression>(seed = 123).toList(), fake<CharProgression>(seed = 123).toList())
        assertEquals(fake<UIntProgression>(seed = 123).toList(), fake<UIntProgression>(seed = 123).toList())
        assertEquals(fake<ULongProgression>(seed = 123).toList(), fake<ULongProgression>(seed = 123).toList())
        assertEquals(fake<Regex>(seed = 123).pattern, fake<Regex>(seed = 123).pattern)
        assertEquals(fake<Duration>(seed = 123), fake<Duration>(seed = 123))
        assertEquals(fake<DurationUnit>(seed = 123), fake<DurationUnit>(seed = 123))
        assertEquals(fake<Instant>(seed = 123), fake<Instant>(seed = 123))
        assertEquals(fake<UByte>(seed = 123), fake<UByte>(seed = 123))
        assertEquals(fake<UShort>(seed = 123), fake<UShort>(seed = 123))
        assertEquals(fake<UInt>(seed = 123), fake<UInt>(seed = 123))
        assertEquals(fake<ULong>(seed = 123), fake<ULong>(seed = 123))
        assertEquals(fake<Uuid>(seed = 123), fake<Uuid>(seed = 123))
        assertEquals(fake<Pair<String, Int>>(seed = 123), fake<Pair<String, Int>>(seed = 123))
        assertEquals(fake<Triple<String, Int, Boolean>>(seed = 123), fake<Triple<String, Int, Boolean>>(seed = 123))
        assertEquals(fake<Result<String>>(seed = 123).toString(), fake<Result<String>>(seed = 123).toString())
    }

    @Test
    fun `fake changes generated values when the seed changes`() {
        assertNotEquals(fake<String>(seed = 123), fake<String>(seed = 456))
        assertNotEquals(fake<Byte>(seed = 123), fake<Byte>(seed = 456))
        assertNotEquals(fake<Short>(seed = 123), fake<Short>(seed = 456))
        assertNotEquals(fake<Int>(seed = 123), fake<Int>(seed = 456))
        assertNotEquals(fake<Long>(seed = 123), fake<Long>(seed = 456))
        assertNotEquals(fake<Float>(seed = 123), fake<Float>(seed = 456))
        assertNotEquals(fake<Double>(seed = 123), fake<Double>(seed = 456))
        assertNotEquals(fake<Char>(seed = 123), fake<Char>(seed = 456))
        assertNotEquals(fake<Duration>(seed = 123), fake<Duration>(seed = 456))
        assertNotEquals(fake<Instant>(seed = 123), fake<Instant>(seed = 456))
        assertNotEquals(fake<UByte>(seed = 123), fake<UByte>(seed = 456))
        assertNotEquals(fake<UShort>(seed = 123), fake<UShort>(seed = 456))
        assertNotEquals(fake<UInt>(seed = 123), fake<UInt>(seed = 456))
        assertNotEquals(fake<ULong>(seed = 123), fake<ULong>(seed = 456))
        assertNotEquals(fake<Uuid>(seed = 123), fake<Uuid>(seed = 456))
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
