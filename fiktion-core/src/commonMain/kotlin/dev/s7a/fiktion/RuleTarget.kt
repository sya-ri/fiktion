package dev.s7a.fiktion

import dev.s7a.fiktion.generators.byte
import dev.s7a.fiktion.generators.char
import dev.s7a.fiktion.generators.double
import dev.s7a.fiktion.generators.float
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.long
import dev.s7a.fiktion.generators.short
import dev.s7a.fiktion.generators.ubyte
import dev.s7a.fiktion.generators.uint
import dev.s7a.fiktion.generators.ulong
import dev.s7a.fiktion.generators.ushort
import kotlin.jvm.JvmName

/**
 * Target selected by a rule declaration.
 */
public sealed interface RuleTarget<T>

/**
 * Groups declarations for this rule target.
 */
public operator fun <T> RuleTarget<T>.invoke(configure: RuleTarget<T>.() -> Unit): RuleTarget<T> {
    configure()
    return this
}

/**
 * Generates [value] for this rule target.
 */
public infix fun <T> RuleTarget<T>.generates(value: T): GenerationSpec<T> = generatesBy { value }

/**
 * Generates values for this rule target by invoking [generator].
 */
public infix fun <T> RuleTarget<T>.generatesBy(generator: Generator<T>): GenerationSpec<T> =
    (this as DefaultRuleTarget<T>).generatesBy(generator)

/**
 * Configures built-in or add-on generator behavior for this rule target.
 */
public fun <T, Value : Any> RuleTarget<T>.using(
    key: FiktionConfig<in T, Value>,
    value: Value,
) {
    using(key(value))
}

/**
 * Configures built-in or add-on generator behavior for this rule target.
 */
public infix fun <T, Value : Any> RuleTarget<T>.using(value: FiktionConfigSetting<in T, Value>) {
    (this as DefaultRuleTarget<T>).config(value)
}

/**
 * Configures built-in or add-on generator behavior for this rule target.
 */
public infix fun <T> RuleTarget<T>.using(value: FiktionConfigSettingGroup<in T>) {
    (this as DefaultRuleTarget<T>).config(value)
}

/**
 * Generates integer values within [range].
 */
public infix fun RuleTarget<Int>.generatesIn(range: IntRange): GenerationSpec<Int> =
    generatesBy {
        int(range)
    }

/**
 * Generates byte values within [range].
 */
@JvmName("generatesByteIn")
public infix fun RuleTarget<Byte>.generatesIn(range: IntRange): GenerationSpec<Byte> =
    generatesBy {
        byte(range)
    }

/**
 * Generates short values within [range].
 */
@JvmName("generatesShortIn")
public infix fun RuleTarget<Short>.generatesIn(range: IntRange): GenerationSpec<Short> =
    generatesBy {
        short(range)
    }

/**
 * Generates long values within [range].
 */
public infix fun RuleTarget<Long>.generatesIn(range: LongRange): GenerationSpec<Long> =
    generatesBy {
        long(range)
    }

/**
 * Generates float values within [range].
 */
@JvmName("generatesFloatIn")
public infix fun RuleTarget<Float>.generatesIn(range: ClosedFloatingPointRange<Float>): GenerationSpec<Float> =
    generatesBy {
        float(range)
    }

/**
 * Generates double values within [range].
 */
@JvmName("generatesDoubleIn")
public infix fun RuleTarget<Double>.generatesIn(range: ClosedFloatingPointRange<Double>): GenerationSpec<Double> =
    generatesBy {
        double(range)
    }

/**
 * Generates character values within [range].
 */
public infix fun RuleTarget<Char>.generatesIn(range: CharRange): GenerationSpec<Char> =
    generatesBy {
        char(range)
    }

/**
 * Generates unsigned byte values within [range].
 */
@JvmName("generatesUByteIn")
public infix fun RuleTarget<UByte>.generatesIn(range: UIntRange): GenerationSpec<UByte> =
    generatesBy {
        ubyte(range)
    }

/**
 * Generates unsigned short values within [range].
 */
@JvmName("generatesUShortIn")
public infix fun RuleTarget<UShort>.generatesIn(range: UIntRange): GenerationSpec<UShort> =
    generatesBy {
        ushort(range)
    }

/**
 * Generates unsigned integer values within [range].
 */
public infix fun RuleTarget<UInt>.generatesIn(range: UIntRange): GenerationSpec<UInt> =
    generatesBy {
        uint(range)
    }

/**
 * Generates unsigned long values within [range].
 */
public infix fun RuleTarget<ULong>.generatesIn(range: ULongRange): GenerationSpec<ULong> =
    generatesBy {
        ulong(range)
    }

/**
 * Generates one value from [values].
 */
public infix fun <T> RuleTarget<T>.generatesOneOf(values: Iterable<T>): GenerationSpec<T> =
    (this as DefaultRuleTarget<T>).generatesOneOf(values.toList())

/**
 * Uses Fiktion's automatic generation for this rule target.
 */
@Suppress("UNUSED_PARAMETER")
public infix fun <T> RuleTarget<T>.generates(auto: Auto): GenerationSpec<T> = (this as DefaultRuleTarget<T>).generatesAutomatically()

/**
 * Uses the constructor default value for this rule target.
 */
@Suppress("UNUSED_PARAMETER")
public infix fun <T> RuleTarget<T>.generates(default: Default): GenerationSpec<T> = (this as DefaultRuleTarget<T>).generatesDefault()
