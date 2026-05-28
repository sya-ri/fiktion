@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalTime::class, ExperimentalUuidApi::class)

package dev.s7a.fiktion

import dev.s7a.fiktion.generators.arithmeticException
import dev.s7a.fiktion.generators.assertionError
import dev.s7a.fiktion.generators.boolean
import dev.s7a.fiktion.generators.booleanArray
import dev.s7a.fiktion.generators.byte
import dev.s7a.fiktion.generators.byteArray
import dev.s7a.fiktion.generators.char
import dev.s7a.fiktion.generators.charArray
import dev.s7a.fiktion.generators.charProgression
import dev.s7a.fiktion.generators.charRange
import dev.s7a.fiktion.generators.classCastException
import dev.s7a.fiktion.generators.concurrentModificationException
import dev.s7a.fiktion.generators.double
import dev.s7a.fiktion.generators.doubleArray
import dev.s7a.fiktion.generators.duration
import dev.s7a.fiktion.generators.durationUnit
import dev.s7a.fiktion.generators.error
import dev.s7a.fiktion.generators.exception
import dev.s7a.fiktion.generators.float
import dev.s7a.fiktion.generators.floatArray
import dev.s7a.fiktion.generators.illegalArgumentException
import dev.s7a.fiktion.generators.illegalStateException
import dev.s7a.fiktion.generators.indexOutOfBoundsException
import dev.s7a.fiktion.generators.instant
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.intArray
import dev.s7a.fiktion.generators.intProgression
import dev.s7a.fiktion.generators.intRange
import dev.s7a.fiktion.generators.list
import dev.s7a.fiktion.generators.long
import dev.s7a.fiktion.generators.longArray
import dev.s7a.fiktion.generators.longProgression
import dev.s7a.fiktion.generators.longRange
import dev.s7a.fiktion.generators.map
import dev.s7a.fiktion.generators.mutableList
import dev.s7a.fiktion.generators.mutableMap
import dev.s7a.fiktion.generators.mutableSet
import dev.s7a.fiktion.generators.noSuchElementException
import dev.s7a.fiktion.generators.nullPointerException
import dev.s7a.fiktion.generators.numberFormatException
import dev.s7a.fiktion.generators.regex
import dev.s7a.fiktion.generators.runtimeException
import dev.s7a.fiktion.generators.sequence
import dev.s7a.fiktion.generators.set
import dev.s7a.fiktion.generators.short
import dev.s7a.fiktion.generators.shortArray
import dev.s7a.fiktion.generators.string
import dev.s7a.fiktion.generators.throwable
import dev.s7a.fiktion.generators.ubyte
import dev.s7a.fiktion.generators.ubyteArray
import dev.s7a.fiktion.generators.uint
import dev.s7a.fiktion.generators.uintArray
import dev.s7a.fiktion.generators.uintProgression
import dev.s7a.fiktion.generators.uintRange
import dev.s7a.fiktion.generators.ulong
import dev.s7a.fiktion.generators.ulongArray
import dev.s7a.fiktion.generators.ulongProgression
import dev.s7a.fiktion.generators.ulongRange
import dev.s7a.fiktion.generators.unit
import dev.s7a.fiktion.generators.unsupportedOperationException
import dev.s7a.fiktion.generators.ushort
import dev.s7a.fiktion.generators.ushortArray
import dev.s7a.fiktion.generators.uuid
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Rules provided by Fiktion core.
 */
internal val BUILT_IN_RULES: List<DefaultGenerationSpec<*>> by lazy {
    DefaultFiktionBuilder()
        .apply {
            type<Unit>() generatesBy {
                unit()
            }
            type<String>() generatesBy {
                string()
            }
            type<Byte>() generatesBy {
                byte()
            }
            type<Short>() generatesBy {
                short()
            }
            type<Int>() generatesBy {
                int()
            }
            type<Long>() generatesBy {
                long()
            }
            type<Float>() generatesBy {
                float()
            }
            type<Double>() generatesBy {
                double()
            }
            type<Boolean>() generatesBy {
                boolean()
            }
            type<Char>() generatesBy {
                char()
            }
            type<ByteArray>() generatesBy {
                byteArray()
            }
            type<ShortArray>() generatesBy {
                shortArray()
            }
            type<IntArray>() generatesBy {
                intArray()
            }
            type<LongArray>() generatesBy {
                longArray()
            }
            type<FloatArray>() generatesBy {
                floatArray()
            }
            type<DoubleArray>() generatesBy {
                doubleArray()
            }
            type<BooleanArray>() generatesBy {
                booleanArray()
            }
            type<CharArray>() generatesBy {
                charArray()
            }
            type<UByteArray>() generatesBy {
                ubyteArray()
            }
            type<UShortArray>() generatesBy {
                ushortArray()
            }
            type<UIntArray>() generatesBy {
                uintArray()
            }
            type<ULongArray>() generatesBy {
                ulongArray()
            }
            type<IntRange>() generatesBy {
                intRange()
            }
            type<LongRange>() generatesBy {
                longRange()
            }
            type<CharRange>() generatesBy {
                charRange()
            }
            type<UIntRange>() generatesBy {
                uintRange()
            }
            type<ULongRange>() generatesBy {
                ulongRange()
            }
            type<IntProgression>() generatesBy {
                intProgression()
            }
            type<LongProgression>() generatesBy {
                longProgression()
            }
            type<CharProgression>() generatesBy {
                charProgression()
            }
            type<UIntProgression>() generatesBy {
                uintProgression()
            }
            type<ULongProgression>() generatesBy {
                ulongProgression()
            }
            type<Regex>() generatesBy {
                regex()
            }
            type<Duration>() generatesBy {
                duration()
            }
            type<DurationUnit>() generatesBy {
                durationUnit()
            }
            type<Instant>() generatesBy {
                instant()
            }
            type<UByte>() generatesBy {
                ubyte()
            }
            type<UShort>() generatesBy {
                ushort()
            }
            type<UInt>() generatesBy {
                uint()
            }
            type<ULong>() generatesBy {
                ulong()
            }
            type<Uuid>() generatesBy {
                uuid()
            }
            type<Throwable>() generatesBy {
                throwable()
            }
            type<Error>() generatesBy {
                error()
            }
            type<Exception>() generatesBy {
                exception()
            }
            type<RuntimeException>() generatesBy {
                runtimeException()
            }
            type<IllegalStateException>() generatesBy {
                illegalStateException()
            }
            type<IllegalArgumentException>() generatesBy {
                illegalArgumentException()
            }
            type<IndexOutOfBoundsException>() generatesBy {
                indexOutOfBoundsException()
            }
            type<ConcurrentModificationException>() generatesBy {
                concurrentModificationException()
            }
            type<UnsupportedOperationException>() generatesBy {
                unsupportedOperationException()
            }
            type<NumberFormatException>() generatesBy {
                numberFormatException()
            }
            type<NullPointerException>() generatesBy {
                nullPointerException()
            }
            type<ClassCastException>() generatesBy {
                classCastException()
            }
            type<AssertionError>() generatesBy {
                assertionError()
            }
            type<NoSuchElementException>() generatesBy {
                noSuchElementException()
            }
            type<ArithmeticException>() generatesBy {
                arithmeticException()
            }
            typeFamily<List<*>>() generatesBy {
                list()
            }
            typeFamily<MutableList<*>>() generatesBy {
                mutableList()
            }
            typeFamily<Set<*>>() generatesBy {
                set()
            }
            typeFamily<MutableSet<*>>() generatesBy {
                mutableSet()
            }
            typeFamily<Map<*, *>>() generatesBy {
                map()
            }
            typeFamily<MutableMap<*, *>>() generatesBy {
                mutableMap()
            }
            typeFamily<Sequence<*>>() generatesBy {
                sequence()
            }
            typeFamily<Pair<*, *>>() generatesBy {
                Pair(
                    fake(argumentIndex = 0, seedIndex = 0),
                    fake(argumentIndex = 1, seedIndex = 1),
                )
            }
            typeFamily<Triple<*, *, *>>() generatesBy {
                Triple(
                    fake(argumentIndex = 0, seedIndex = 0),
                    fake(argumentIndex = 1, seedIndex = 1),
                    fake(argumentIndex = 2, seedIndex = 2),
                )
            }
            typeFamily<Result<*>>() generatesBy {
                if (boolean()) {
                    Result.success(fake(0))
                } else {
                    Result.failure(exception())
                }
            }
        }.build()
        .rules
        .map { rule -> rule.snapshot(precedence = RulePrecedence.BUILT_IN) }
}
