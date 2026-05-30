package dev.s7a.fiktion

import dev.s7a.fiktion.generators.FiktionCharset
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.time.Duration.Companion.milliseconds
import kotlin.Array as KotlinArray
import kotlin.Byte as KotlinByte
import kotlin.Char as KotlinChar
import kotlin.Double as KotlinDouble
import kotlin.Float as KotlinFloat
import kotlin.Int as KotlinInt
import kotlin.Long as KotlinLong
import kotlin.Short as KotlinShort
import kotlin.String as KotlinString
import kotlin.UByte as KotlinUByte
import kotlin.UInt as KotlinUInt
import kotlin.ULong as KotlinULong
import kotlin.UShort as KotlinUShort
import kotlin.collections.Collection as KotlinCollection
import kotlin.collections.Map as KotlinMap
import kotlin.ranges.CharProgression as KotlinCharProgression
import kotlin.ranges.IntProgression as KotlinIntProgression
import kotlin.ranges.IntRange as KotlinIntRange
import kotlin.ranges.LongProgression as KotlinLongProgression
import kotlin.ranges.LongRange as KotlinLongRange
import kotlin.ranges.UIntProgression as KotlinUIntProgression
import kotlin.ranges.UIntRange as KotlinUIntRange
import kotlin.ranges.ULongProgression as KotlinULongProgression
import kotlin.ranges.ULongRange as KotlinULongRange
import kotlin.time.Duration as KotlinDuration
import kotlin.time.Instant as KotlinInstant

/**
 * Typed key for configuring built-in and add-on generators.
 */
public class FiktionConfig<Scope, Value : Any>
    @PublishedApi
    internal constructor(
        /**
         * Generated value type this configuration applies to.
         */
        internal val scopeType: KType,
        /**
         * Value used when no matching configuration is registered.
         */
        public val defaultValue: Value,
    ) {
        public companion object {
            public inline operator fun <reified Scope, Value : Any> invoke(defaultValue: Value): FiktionConfig<Scope, Value> =
                FiktionConfig(scopeType = typeOf<Scope>(), defaultValue = defaultValue)
        }

        /**
         * Integer generator configuration.
         */
        public object Int {
            public val range: FiktionConfig<KotlinInt, KotlinIntRange> =
                FiktionConfig(KotlinInt.MIN_VALUE..KotlinInt.MAX_VALUE)
        }

        /**
         * Integer range generator configuration.
         */
        public object IntRange {
            public val bounds: FiktionConfig<KotlinIntRange, KotlinIntRange> =
                FiktionConfig(KotlinInt.MIN_VALUE..KotlinInt.MAX_VALUE)
        }

        /**
         * Integer progression generator configuration.
         */
        public object IntProgression {
            public val bounds: FiktionConfig<KotlinIntProgression, KotlinIntRange> =
                FiktionConfig(-100..100)

            public val step: FiktionConfig<KotlinIntProgression, KotlinIntRange> =
                FiktionConfig(1..5)
        }

        /**
         * Byte generator configuration.
         */
        public object Byte {
            public val range: FiktionConfig<KotlinByte, KotlinIntRange> =
                FiktionConfig(KotlinByte.MIN_VALUE..KotlinByte.MAX_VALUE)
        }

        /**
         * Short generator configuration.
         */
        public object Short {
            public val range: FiktionConfig<KotlinShort, KotlinIntRange> =
                FiktionConfig(KotlinShort.MIN_VALUE..KotlinShort.MAX_VALUE)
        }

        /**
         * Long generator configuration.
         */
        public object Long {
            public val range: FiktionConfig<KotlinLong, KotlinLongRange> =
                FiktionConfig(KotlinLong.MIN_VALUE..KotlinLong.MAX_VALUE)
        }

        /**
         * Long range generator configuration.
         */
        public object LongRange {
            public val bounds: FiktionConfig<KotlinLongRange, KotlinLongRange> =
                FiktionConfig(KotlinLong.MIN_VALUE..KotlinLong.MAX_VALUE)
        }

        /**
         * Long progression generator configuration.
         */
        public object LongProgression {
            public val bounds: FiktionConfig<KotlinLongProgression, KotlinLongRange> =
                FiktionConfig(-100L..100L)

            public val step: FiktionConfig<KotlinLongProgression, KotlinLongRange> =
                FiktionConfig(1L..5L)
        }

        /**
         * Unsigned byte generator configuration.
         */
        public object UByte {
            public val range: FiktionConfig<KotlinUByte, KotlinUIntRange> =
                FiktionConfig(KotlinUByte.MIN_VALUE..KotlinUByte.MAX_VALUE)
        }

        /**
         * Unsigned short generator configuration.
         */
        public object UShort {
            public val range: FiktionConfig<KotlinUShort, KotlinUIntRange> =
                FiktionConfig(KotlinUShort.MIN_VALUE..KotlinUShort.MAX_VALUE)
        }

        /**
         * Unsigned integer generator configuration.
         */
        public object UInt {
            public val range: FiktionConfig<KotlinUInt, KotlinUIntRange> =
                FiktionConfig(KotlinUInt.MIN_VALUE..KotlinUInt.MAX_VALUE)
        }

        /**
         * Unsigned integer range generator configuration.
         */
        public object UIntRange {
            public val bounds: FiktionConfig<KotlinUIntRange, KotlinUIntRange> =
                FiktionConfig(KotlinUInt.MIN_VALUE..KotlinUInt.MAX_VALUE)
        }

        /**
         * Unsigned integer progression generator configuration.
         */
        public object UIntProgression {
            public val bounds: FiktionConfig<KotlinUIntProgression, KotlinUIntRange> =
                FiktionConfig(0u..100u)

            public val step: FiktionConfig<KotlinUIntProgression, KotlinIntRange> =
                FiktionConfig(1..5)
        }

        /**
         * Unsigned long generator configuration.
         */
        public object ULong {
            public val range: FiktionConfig<KotlinULong, KotlinULongRange> =
                FiktionConfig(KotlinULong.MIN_VALUE..KotlinULong.MAX_VALUE)
        }

        /**
         * Unsigned long range generator configuration.
         */
        public object ULongRange {
            public val bounds: FiktionConfig<KotlinULongRange, KotlinULongRange> =
                FiktionConfig(KotlinULong.MIN_VALUE..KotlinULong.MAX_VALUE)
        }

        /**
         * Unsigned long progression generator configuration.
         */
        public object ULongProgression {
            public val bounds: FiktionConfig<KotlinULongProgression, KotlinULongRange> =
                FiktionConfig(0uL..100uL)

            public val step: FiktionConfig<KotlinULongProgression, KotlinLongRange> =
                FiktionConfig(1L..5L)
        }

        /**
         * Float generator configuration.
         */
        public object Float {
            public val range: FiktionConfig<KotlinFloat, ClosedFloatingPointRange<KotlinFloat>> =
                FiktionConfig(0.0f..1.0f)
        }

        /**
         * Double generator configuration.
         */
        public object Double {
            public val range: FiktionConfig<KotlinDouble, ClosedFloatingPointRange<KotlinDouble>> =
                FiktionConfig(0.0..1.0)
        }

        /**
         * Character generator configuration.
         */
        public object Char {
            public val charset: FiktionConfig<KotlinChar, FiktionCharset> =
                FiktionConfig(FiktionCharset.AlphaNumeric)

            public val rangeCharsets: FiktionConfig<CharRange, List<FiktionCharset>> =
                FiktionConfig(
                    listOf(FiktionCharset.LowercaseAlpha, FiktionCharset.UppercaseAlpha, FiktionCharset.Numeric),
                )

            public val step: FiktionConfig<KotlinCharProgression, KotlinIntRange> =
                FiktionConfig(1..5)
        }

        /**
         * String generator configuration.
         */
        public object String {
            public val length: FiktionConfig<KotlinString, KotlinIntRange> =
                FiktionConfig(1..32)

            public val charset: FiktionConfig<KotlinString, FiktionCharset> =
                FiktionConfig(FiktionCharset.AlphaNumeric)
        }

        /**
         * Collection generator configuration.
         */
        public object Collection {
            public val size: FiktionConfig<KotlinCollection<*>, KotlinIntRange> =
                FiktionConfig(DEFAULT_COLLECTION_SIZE_RANGE)
        }

        /**
         * Map generator configuration.
         */
        public object Map {
            public val size: FiktionConfig<KotlinMap<*, *>, KotlinIntRange> =
                FiktionConfig(DEFAULT_MAP_SIZE_RANGE)
        }

        /**
         * Array generator configuration.
         */
        public object Array {
            public val size: FiktionConfig<KotlinArray<*>, KotlinIntRange> =
                FiktionConfig(1..3)
        }

        /**
         * Regex generator configuration.
         */
        public object Regex {
            public val length: FiktionConfig<kotlin.text.Regex, KotlinIntRange> =
                FiktionConfig(8..8)

            public val charset: FiktionConfig<kotlin.text.Regex, FiktionCharset> =
                FiktionConfig(FiktionCharset.AlphaNumeric)
        }

        /**
         * Duration generator configuration.
         */
        public object Duration {
            public val range: FiktionConfig<KotlinDuration, ClosedRange<KotlinDuration>> =
                FiktionConfig((-3_153_600_000_000L).milliseconds..3_153_600_000_000L.milliseconds)
        }

        /**
         * Instant generator configuration.
         */
        public object Instant {
            public val epochSeconds: FiktionConfig<KotlinInstant, KotlinLongRange> =
                FiktionConfig(946_684_800L..<4_102_444_800L)

            public val nanosecond: FiktionConfig<KotlinInstant, KotlinIntRange> =
                FiktionConfig(0..<1_000_000_000)
        }
    }

/**
 * Typed generator configuration value.
 */
public data class FiktionConfigSetting<Scope, Value : Any>(
    public val key: FiktionConfig<Scope, Value>,
    public val value: Value,
)

/**
 * Creates a typed generator configuration value for this key.
 */
public operator fun <Scope, Value : Any> FiktionConfig<Scope, Value>.invoke(value: Value): FiktionConfigSetting<Scope, Value> =
    FiktionConfigSetting(key = this, value = value)
