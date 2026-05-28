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
import dev.s7a.fiktion.generators.long
import dev.s7a.fiktion.generators.longArray
import dev.s7a.fiktion.generators.longProgression
import dev.s7a.fiktion.generators.longRange
import dev.s7a.fiktion.generators.noSuchElementException
import dev.s7a.fiktion.generators.nullPointerException
import dev.s7a.fiktion.generators.numberFormatException
import dev.s7a.fiktion.generators.pair
import dev.s7a.fiktion.generators.regex
import dev.s7a.fiktion.generators.result
import dev.s7a.fiktion.generators.runtimeException
import dev.s7a.fiktion.generators.short
import dev.s7a.fiktion.generators.shortArray
import dev.s7a.fiktion.generators.string
import dev.s7a.fiktion.generators.throwable
import dev.s7a.fiktion.generators.triple
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
import kotlin.reflect.KType
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Generates values for Kotlin built-in types.
 */
internal fun generateBuiltIn(
    type: KType,
    context: FakeContext,
    config: FiktionConfig,
): Any =
    when (type.classifier) {
        Unit::class -> {
            context.unit()
        }

        String::class -> {
            context.string()
        }

        Byte::class -> {
            context.byte()
        }

        Short::class -> {
            context.short()
        }

        Int::class -> {
            context.int()
        }

        Long::class -> {
            context.long()
        }

        Float::class -> {
            context.float()
        }

        Double::class -> {
            context.double()
        }

        Boolean::class -> {
            context.boolean()
        }

        Char::class -> {
            context.char()
        }

        ByteArray::class -> {
            context.byteArray()
        }

        ShortArray::class -> {
            context.shortArray()
        }

        IntArray::class -> {
            context.intArray()
        }

        LongArray::class -> {
            context.longArray()
        }

        FloatArray::class -> {
            context.floatArray()
        }

        DoubleArray::class -> {
            context.doubleArray()
        }

        BooleanArray::class -> {
            context.booleanArray()
        }

        CharArray::class -> {
            context.charArray()
        }

        UByteArray::class -> {
            context.ubyteArray()
        }

        UShortArray::class -> {
            context.ushortArray()
        }

        UIntArray::class -> {
            context.uintArray()
        }

        ULongArray::class -> {
            context.ulongArray()
        }

        IntRange::class -> {
            context.intRange()
        }

        LongRange::class -> {
            context.longRange()
        }

        CharRange::class -> {
            context.charRange()
        }

        UIntRange::class -> {
            context.uintRange()
        }

        ULongRange::class -> {
            context.ulongRange()
        }

        IntProgression::class -> {
            context.intProgression()
        }

        LongProgression::class -> {
            context.longProgression()
        }

        CharProgression::class -> {
            context.charProgression()
        }

        UIntProgression::class -> {
            context.uintProgression()
        }

        ULongProgression::class -> {
            context.ulongProgression()
        }

        Regex::class -> {
            context.regex()
        }

        Duration::class -> {
            context.duration()
        }

        DurationUnit::class -> {
            context.durationUnit()
        }

        Instant::class -> {
            context.instant()
        }

        UByte::class -> {
            context.ubyte()
        }

        UShort::class -> {
            context.ushort()
        }

        UInt::class -> {
            context.uint()
        }

        ULong::class -> {
            context.ulong()
        }

        Uuid::class -> {
            context.uuid()
        }

        Throwable::class -> {
            context.throwable()
        }

        Error::class -> {
            context.error()
        }

        Exception::class -> {
            context.exception()
        }

        RuntimeException::class -> {
            context.runtimeException()
        }

        IllegalStateException::class -> {
            context.illegalStateException()
        }

        IllegalArgumentException::class -> {
            context.illegalArgumentException()
        }

        IndexOutOfBoundsException::class -> {
            context.indexOutOfBoundsException()
        }

        ConcurrentModificationException::class -> {
            context.concurrentModificationException()
        }

        UnsupportedOperationException::class -> {
            context.unsupportedOperationException()
        }

        NumberFormatException::class -> {
            context.numberFormatException()
        }

        NullPointerException::class -> {
            context.nullPointerException()
        }

        ClassCastException::class -> {
            context.classCastException()
        }

        AssertionError::class -> {
            context.assertionError()
        }

        NoSuchElementException::class -> {
            context.noSuchElementException()
        }

        ArithmeticException::class -> {
            context.arithmeticException()
        }

        Pair::class -> {
            context.pair(
                first = { generateBuiltInTypeArgument(type = type, index = 0, config = config, seed = this.seed, depth = this.depth) },
                second = { generateBuiltInTypeArgument(type = type, index = 1, config = config, seed = this.seed, depth = this.depth) },
            )
        }

        Triple::class -> {
            context.triple(
                first = { generateBuiltInTypeArgument(type = type, index = 0, config = config, seed = this.seed, depth = this.depth) },
                second = { generateBuiltInTypeArgument(type = type, index = 1, config = config, seed = this.seed, depth = this.depth) },
                third = { generateBuiltInTypeArgument(type = type, index = 2, config = config, seed = this.seed, depth = this.depth) },
            )
        }

        Result::class -> {
            context.result {
                generateBuiltInTypeArgument(type = type, index = 0, config = config, seed = this.seed, depth = this.depth)
            }
        }

        else -> {
            throw CannotGenerateException(missingGenerationMessage(type))
        }
    }

/**
 * Generates a value for [type]'s type argument at [index].
 */
private fun generateBuiltInTypeArgument(
    type: KType,
    index: Int,
    config: FiktionConfig,
    seed: Long,
    depth: Int,
): Any? {
    val argumentType =
        type.arguments.getOrNull(index)?.type
            ?: throw CannotGenerateException("Cannot generate $type because type argument $index is unavailable.")
    return generateValue(
        request = GenerationRequest(type = argumentType),
        config = config,
        seed = seed,
        depth = depth,
    )
}
