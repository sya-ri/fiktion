package dev.s7a.fiktion

import dev.s7a.fiktion.generators.FiktionCharset
import kotlin.jvm.JvmName
import kotlin.ranges.ClosedRange
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
public class FiktionConfig<Scope, Value : Any>(
    /**
     * Value used when no matching configuration is registered.
     */
    public val defaultValue: Value,
) {
    internal constructor(
        defaultValue: Value,
        validate: (Value) -> Unit,
    ) : this(defaultValue) {
        validate(defaultValue)
        validateValue = validate
    }

    internal var validateValue: (Value) -> Unit = {}

    /**
     * Returns the effective generator configuration value for the current generation context.
     */
    context(context: FakeContext)
    public fun get(): Value = context.getConfig(this)

    /**
     * Creates a typed generator configuration value for this key.
     */
    public operator fun invoke(value: Value): FiktionConfigSetting<Scope, Value> =
        FiktionConfigSetting(key = this, value = value).also { validateValue(value) }

    /**
     * Integer generator configuration.
     */
    public object Int {
        /**
         * Minimum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val min: FiktionConfig<KotlinInt, KotlinInt> =
            FiktionConfig(KotlinInt.MIN_VALUE)

        /**
         * Maximum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val max: FiktionConfig<KotlinInt, KotlinInt> =
            FiktionConfig(KotlinInt.MAX_VALUE)

        /**
         * Candidate ranges excluded after min and max are composed.
         */
        public val excluding: FiktionConfig<KotlinInt, List<ClosedRange<KotlinInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace the candidate range endpoints with [value] and clear configured exclusions.
         */
        public fun range(value: ClosedRange<KotlinInt>): FiktionConfigSettings<KotlinInt> =
            FiktionConfigSettings(listOf(min(value.start), max(value.endInclusive), excluding(emptyList())))

        /**
         * Creates settings that fix the candidate range to [value] and clear configured exclusions.
         */
        public fun range(value: KotlinInt): FiktionConfigSettings<KotlinInt> = range(value..value)

        /**
         * Samples a value from the composed range after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun range(): KotlinInt = context.candidate(min, max, excluding)
    }

    /**
     * Integer range generator configuration.
     */
    public object IntRange {
        /**
         * Minimum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val minBound: FiktionConfig<KotlinIntRange, KotlinInt> =
            FiktionConfig(KotlinInt.MIN_VALUE)

        /**
         * Maximum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val maxBound: FiktionConfig<KotlinIntRange, KotlinInt> =
            FiktionConfig(KotlinInt.MAX_VALUE)

        /**
         * Candidate bounds excluded after minBound and maxBound are composed.
         */
        public val excludingBounds: FiktionConfig<KotlinIntRange, List<ClosedRange<KotlinInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated bound endpoints with [value] and clear configured bound exclusions.
         */
        public fun bounds(value: ClosedRange<KotlinInt>): FiktionConfigSettings<KotlinIntRange> =
            FiktionConfigSettings(listOf(minBound(value.start), maxBound(value.endInclusive), excludingBounds(emptyList())))

        /**
         * Creates settings that fix generated range bounds to [value] and clear configured bound exclusions.
         */
        public fun bounds(value: KotlinInt): FiktionConfigSettings<KotlinIntRange> = bounds(value..value)

        /**
         * Samples two bounds from the composed candidate range and returns them as an ascending range.
         *
         * Throws [FiktionConfigurationException] when the composed bound candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun bounds(): KotlinIntRange {
            val bounds = context.bounds(minBound, maxBound, excludingBounds)
            return bounds.start..bounds.endInclusive
        }
    }

    /**
     * Integer progression generator configuration.
     */
    public object IntProgression {
        /**
         * Minimum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val minBound: FiktionConfig<KotlinIntProgression, KotlinInt> =
            FiktionConfig(-100)

        /**
         * Maximum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val maxBound: FiktionConfig<KotlinIntProgression, KotlinInt> =
            FiktionConfig(100)

        /**
         * Minimum candidate step for generated progressions. The composed step range is checked when candidates are sampled.
         */
        public val minStep: FiktionConfig<KotlinIntProgression, KotlinInt> =
            FiktionConfig(1, ::requirePositive)

        /**
         * Maximum candidate step for generated progressions. The composed step range is checked when candidates are sampled.
         */
        public val maxStep: FiktionConfig<KotlinIntProgression, KotlinInt> =
            FiktionConfig(5, ::requirePositive)

        /**
         * Candidate bounds excluded after minBound and maxBound are composed.
         */
        public val excludingBounds: FiktionConfig<KotlinIntProgression, List<ClosedRange<KotlinInt>>> =
            exclusionsConfig()

        /**
         * Candidate steps excluded after minStep and maxStep are composed.
         */
        public val excludingSteps: FiktionConfig<KotlinIntProgression, List<ClosedRange<KotlinInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated bound endpoints with [value] and clear configured bound exclusions.
         */
        public fun bounds(value: ClosedRange<KotlinInt>): FiktionConfigSettings<KotlinIntProgression> =
            FiktionConfigSettings(listOf(minBound(value.start), maxBound(value.endInclusive), excludingBounds(emptyList())))

        /**
         * Creates settings that fix generated range bounds to [value] and clear configured bound exclusions.
         */
        public fun bounds(value: KotlinInt): FiktionConfigSettings<KotlinIntProgression> = bounds(value..value)

        /**
         * Creates settings that replace generated step endpoints with [value] and clear configured step exclusions.
         *
         * Throws [FiktionConfigurationException] when either endpoint is not a valid step.
         */
        public fun step(value: ClosedRange<KotlinInt>): FiktionConfigSettings<KotlinIntProgression> =
            FiktionConfigSettings(listOf(minStep(value.start), maxStep(value.endInclusive), excludingSteps(emptyList())))

        /**
         * Creates settings that fix generated progression steps to [value] and clear configured step exclusions.
         *
         * Throws [FiktionConfigurationException] when [value] is not a valid step.
         */
        public fun step(value: KotlinInt): FiktionConfigSettings<KotlinIntProgression> = step(value..value)

        /**
         * Samples two bounds from the composed candidate range and returns them as an ascending range.
         *
         * Throws [FiktionConfigurationException] when the composed bound candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun bounds(): KotlinIntRange {
            val bounds = context.bounds(minBound, maxBound, excludingBounds)
            return bounds.start..bounds.endInclusive
        }

        /**
         * Samples a step from the composed step candidates after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed step candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun step(): KotlinInt = context.candidate(minStep, maxStep, excludingSteps)
    }

    /**
     * Byte generator configuration.
     */
    public object Byte {
        /**
         * Minimum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val min: FiktionConfig<KotlinByte, KotlinInt> =
            FiktionConfig(KotlinByte.MIN_VALUE.toInt())

        /**
         * Maximum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val max: FiktionConfig<KotlinByte, KotlinInt> =
            FiktionConfig(KotlinByte.MAX_VALUE.toInt())

        /**
         * Candidate ranges excluded after min and max are composed.
         */
        public val excluding: FiktionConfig<KotlinByte, List<ClosedRange<KotlinInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace the candidate range endpoints with [value] and clear configured exclusions.
         */
        public fun range(value: ClosedRange<KotlinInt>): FiktionConfigSettings<KotlinByte> =
            FiktionConfigSettings(listOf(min(value.start), max(value.endInclusive), excluding(emptyList())))

        /**
         * Creates settings that fix the candidate range to [value] and clear configured exclusions.
         */
        public fun range(value: KotlinInt): FiktionConfigSettings<KotlinByte> = range(value..value)

        /**
         * Samples a value from the composed range after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun range(): KotlinInt = context.candidate(min, max, excluding)
    }

    /**
     * Short generator configuration.
     */
    public object Short {
        /**
         * Minimum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val min: FiktionConfig<KotlinShort, KotlinInt> =
            FiktionConfig(KotlinShort.MIN_VALUE.toInt())

        /**
         * Maximum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val max: FiktionConfig<KotlinShort, KotlinInt> =
            FiktionConfig(KotlinShort.MAX_VALUE.toInt())

        /**
         * Candidate ranges excluded after min and max are composed.
         */
        public val excluding: FiktionConfig<KotlinShort, List<ClosedRange<KotlinInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace the candidate range endpoints with [value] and clear configured exclusions.
         */
        public fun range(value: ClosedRange<KotlinInt>): FiktionConfigSettings<KotlinShort> =
            FiktionConfigSettings(listOf(min(value.start), max(value.endInclusive), excluding(emptyList())))

        /**
         * Creates settings that fix the candidate range to [value] and clear configured exclusions.
         */
        public fun range(value: KotlinInt): FiktionConfigSettings<KotlinShort> = range(value..value)

        /**
         * Samples a value from the composed range after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun range(): KotlinInt = context.candidate(min, max, excluding)
    }

    /**
     * Long generator configuration.
     */
    public object Long {
        /**
         * Minimum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val min: FiktionConfig<KotlinLong, KotlinLong> =
            FiktionConfig(KotlinLong.MIN_VALUE)

        /**
         * Maximum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val max: FiktionConfig<KotlinLong, KotlinLong> =
            FiktionConfig(KotlinLong.MAX_VALUE)

        /**
         * Candidate ranges excluded after min and max are composed.
         */
        public val excluding: FiktionConfig<KotlinLong, List<ClosedRange<KotlinLong>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace the candidate range endpoints with [value] and clear configured exclusions.
         */
        public fun range(value: ClosedRange<KotlinLong>): FiktionConfigSettings<KotlinLong> =
            FiktionConfigSettings(listOf(min(value.start), max(value.endInclusive), excluding(emptyList())))

        /**
         * Creates settings that fix the candidate range to [value] and clear configured exclusions.
         */
        public fun range(value: KotlinLong): FiktionConfigSettings<KotlinLong> = range(value..value)

        /**
         * Samples a value from the composed range after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun range(): KotlinLong = context.candidate(min, max, excluding)
    }

    /**
     * Long range generator configuration.
     */
    public object LongRange {
        /**
         * Minimum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val minBound: FiktionConfig<KotlinLongRange, KotlinLong> =
            FiktionConfig(KotlinLong.MIN_VALUE)

        /**
         * Maximum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val maxBound: FiktionConfig<KotlinLongRange, KotlinLong> =
            FiktionConfig(KotlinLong.MAX_VALUE)

        /**
         * Candidate bounds excluded after minBound and maxBound are composed.
         */
        public val excludingBounds: FiktionConfig<KotlinLongRange, List<ClosedRange<KotlinLong>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated bound endpoints with [value] and clear configured bound exclusions.
         */
        public fun bounds(value: ClosedRange<KotlinLong>): FiktionConfigSettings<KotlinLongRange> =
            FiktionConfigSettings(listOf(minBound(value.start), maxBound(value.endInclusive), excludingBounds(emptyList())))

        /**
         * Creates settings that fix generated range bounds to [value] and clear configured bound exclusions.
         */
        public fun bounds(value: KotlinLong): FiktionConfigSettings<KotlinLongRange> = bounds(value..value)

        /**
         * Samples two bounds from the composed candidate range and returns them as an ascending range.
         *
         * Throws [FiktionConfigurationException] when the composed bound candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun bounds(): KotlinLongRange {
            val bounds = context.bounds(minBound, maxBound, excludingBounds)
            return bounds.start..bounds.endInclusive
        }
    }

    /**
     * Long progression generator configuration.
     */
    public object LongProgression {
        /**
         * Minimum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val minBound: FiktionConfig<KotlinLongProgression, KotlinLong> =
            FiktionConfig(-100L)

        /**
         * Maximum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val maxBound: FiktionConfig<KotlinLongProgression, KotlinLong> =
            FiktionConfig(100L)

        /**
         * Minimum candidate step for generated progressions. The composed step range is checked when candidates are sampled.
         */
        public val minStep: FiktionConfig<KotlinLongProgression, KotlinLong> =
            FiktionConfig(1L, ::requirePositive)

        /**
         * Maximum candidate step for generated progressions. The composed step range is checked when candidates are sampled.
         */
        public val maxStep: FiktionConfig<KotlinLongProgression, KotlinLong> =
            FiktionConfig(5L, ::requirePositive)

        /**
         * Candidate bounds excluded after minBound and maxBound are composed.
         */
        public val excludingBounds: FiktionConfig<KotlinLongProgression, List<ClosedRange<KotlinLong>>> =
            exclusionsConfig()

        /**
         * Candidate steps excluded after minStep and maxStep are composed.
         */
        public val excludingSteps: FiktionConfig<KotlinLongProgression, List<ClosedRange<KotlinLong>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated bound endpoints with [value] and clear configured bound exclusions.
         */
        public fun bounds(value: ClosedRange<KotlinLong>): FiktionConfigSettings<KotlinLongProgression> =
            FiktionConfigSettings(listOf(minBound(value.start), maxBound(value.endInclusive), excludingBounds(emptyList())))

        /**
         * Creates settings that fix generated range bounds to [value] and clear configured bound exclusions.
         */
        public fun bounds(value: KotlinLong): FiktionConfigSettings<KotlinLongProgression> = bounds(value..value)

        /**
         * Creates settings that replace generated step endpoints with [value] and clear configured step exclusions.
         *
         * Throws [FiktionConfigurationException] when either endpoint is not a valid step.
         */
        public fun step(value: ClosedRange<KotlinLong>): FiktionConfigSettings<KotlinLongProgression> =
            FiktionConfigSettings(listOf(minStep(value.start), maxStep(value.endInclusive), excludingSteps(emptyList())))

        /**
         * Creates settings that fix generated progression steps to [value] and clear configured step exclusions.
         *
         * Throws [FiktionConfigurationException] when [value] is not a valid step.
         */
        public fun step(value: KotlinLong): FiktionConfigSettings<KotlinLongProgression> = step(value..value)

        /**
         * Samples two bounds from the composed candidate range and returns them as an ascending range.
         *
         * Throws [FiktionConfigurationException] when the composed bound candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun bounds(): KotlinLongRange {
            val bounds = context.bounds(minBound, maxBound, excludingBounds)
            return bounds.start..bounds.endInclusive
        }

        /**
         * Samples a step from the composed step candidates after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed step candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun step(): KotlinLong = context.candidate(minStep, maxStep, excludingSteps)
    }

    /**
     * Unsigned byte generator configuration.
     */
    public object UByte {
        /**
         * Minimum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val min: FiktionConfig<KotlinUByte, KotlinUInt> =
            FiktionConfig(KotlinUByte.MIN_VALUE.toUInt())

        /**
         * Maximum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val max: FiktionConfig<KotlinUByte, KotlinUInt> =
            FiktionConfig(KotlinUByte.MAX_VALUE.toUInt())

        /**
         * Candidate ranges excluded after min and max are composed.
         */
        public val excluding: FiktionConfig<KotlinUByte, List<ClosedRange<KotlinUInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace the candidate range endpoints with [value] and clear configured exclusions.
         */
        public fun range(value: ClosedRange<KotlinUInt>): FiktionConfigSettings<KotlinUByte> =
            FiktionConfigSettings(listOf(min(value.start), max(value.endInclusive), excluding(emptyList())))

        /**
         * Creates settings that fix the candidate range to [value] and clear configured exclusions.
         */
        public fun range(value: KotlinUInt): FiktionConfigSettings<KotlinUByte> = range(value..value)

        /**
         * Samples a value from the composed range after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun range(): KotlinUInt = context.candidate(min, max, excluding)
    }

    /**
     * Unsigned short generator configuration.
     */
    public object UShort {
        /**
         * Minimum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val min: FiktionConfig<KotlinUShort, KotlinUInt> =
            FiktionConfig(KotlinUShort.MIN_VALUE.toUInt())

        /**
         * Maximum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val max: FiktionConfig<KotlinUShort, KotlinUInt> =
            FiktionConfig(KotlinUShort.MAX_VALUE.toUInt())

        /**
         * Candidate ranges excluded after min and max are composed.
         */
        public val excluding: FiktionConfig<KotlinUShort, List<ClosedRange<KotlinUInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace the candidate range endpoints with [value] and clear configured exclusions.
         */
        public fun range(value: ClosedRange<KotlinUInt>): FiktionConfigSettings<KotlinUShort> =
            FiktionConfigSettings(listOf(min(value.start), max(value.endInclusive), excluding(emptyList())))

        /**
         * Creates settings that fix the candidate range to [value] and clear configured exclusions.
         */
        public fun range(value: KotlinUInt): FiktionConfigSettings<KotlinUShort> = range(value..value)

        /**
         * Samples a value from the composed range after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun range(): KotlinUInt = context.candidate(min, max, excluding)
    }

    /**
     * Unsigned integer generator configuration.
     */
    public object UInt {
        /**
         * Minimum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val min: FiktionConfig<KotlinUInt, KotlinUInt> =
            FiktionConfig(KotlinUInt.MIN_VALUE)

        /**
         * Maximum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val max: FiktionConfig<KotlinUInt, KotlinUInt> =
            FiktionConfig(KotlinUInt.MAX_VALUE)

        /**
         * Candidate ranges excluded after min and max are composed.
         */
        public val excluding: FiktionConfig<KotlinUInt, List<ClosedRange<KotlinUInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace the candidate range endpoints with [value] and clear configured exclusions.
         */
        public fun range(value: ClosedRange<KotlinUInt>): FiktionConfigSettings<KotlinUInt> =
            FiktionConfigSettings(listOf(min(value.start), max(value.endInclusive), excluding(emptyList())))

        /**
         * Creates settings that fix the candidate range to [value] and clear configured exclusions.
         */
        public fun range(value: KotlinUInt): FiktionConfigSettings<KotlinUInt> = range(value..value)

        /**
         * Samples a value from the composed range after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun range(): KotlinUInt = context.candidate(min, max, excluding)
    }

    /**
     * Unsigned integer range generator configuration.
     */
    public object UIntRange {
        /**
         * Minimum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val minBound: FiktionConfig<KotlinUIntRange, KotlinUInt> =
            FiktionConfig(KotlinUInt.MIN_VALUE)

        /**
         * Maximum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val maxBound: FiktionConfig<KotlinUIntRange, KotlinUInt> =
            FiktionConfig(KotlinUInt.MAX_VALUE)

        /**
         * Candidate bounds excluded after minBound and maxBound are composed.
         */
        public val excludingBounds: FiktionConfig<KotlinUIntRange, List<ClosedRange<KotlinUInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated bound endpoints with [value] and clear configured bound exclusions.
         */
        public fun bounds(value: ClosedRange<KotlinUInt>): FiktionConfigSettings<KotlinUIntRange> =
            FiktionConfigSettings(listOf(minBound(value.start), maxBound(value.endInclusive), excludingBounds(emptyList())))

        /**
         * Creates settings that fix generated range bounds to [value] and clear configured bound exclusions.
         */
        public fun bounds(value: KotlinUInt): FiktionConfigSettings<KotlinUIntRange> = bounds(value..value)

        /**
         * Samples two bounds from the composed candidate range and returns them as an ascending range.
         *
         * Throws [FiktionConfigurationException] when the composed bound candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun bounds(): KotlinUIntRange {
            val bounds = context.bounds(minBound, maxBound, excludingBounds)
            return bounds.start..bounds.endInclusive
        }
    }

    /**
     * Unsigned integer progression generator configuration.
     */
    public object UIntProgression {
        /**
         * Minimum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val minBound: FiktionConfig<KotlinUIntProgression, KotlinUInt> =
            FiktionConfig(0u)

        /**
         * Maximum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val maxBound: FiktionConfig<KotlinUIntProgression, KotlinUInt> =
            FiktionConfig(100u)

        /**
         * Minimum candidate step for generated progressions. The composed step range is checked when candidates are sampled.
         */
        public val minStep: FiktionConfig<KotlinUIntProgression, KotlinInt> =
            FiktionConfig(1, ::requirePositive)

        /**
         * Maximum candidate step for generated progressions. The composed step range is checked when candidates are sampled.
         */
        public val maxStep: FiktionConfig<KotlinUIntProgression, KotlinInt> =
            FiktionConfig(5, ::requirePositive)

        /**
         * Candidate bounds excluded after minBound and maxBound are composed.
         */
        public val excludingBounds: FiktionConfig<KotlinUIntProgression, List<ClosedRange<KotlinUInt>>> =
            exclusionsConfig()

        /**
         * Candidate steps excluded after minStep and maxStep are composed.
         */
        public val excludingSteps: FiktionConfig<KotlinUIntProgression, List<ClosedRange<KotlinInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated bound endpoints with [value] and clear configured bound exclusions.
         */
        public fun bounds(value: ClosedRange<KotlinUInt>): FiktionConfigSettings<KotlinUIntProgression> =
            FiktionConfigSettings(listOf(minBound(value.start), maxBound(value.endInclusive), excludingBounds(emptyList())))

        /**
         * Creates settings that fix generated range bounds to [value] and clear configured bound exclusions.
         */
        public fun bounds(value: KotlinUInt): FiktionConfigSettings<KotlinUIntProgression> = bounds(value..value)

        /**
         * Creates settings that replace generated step endpoints with [value] and clear configured step exclusions.
         *
         * Throws [FiktionConfigurationException] when either endpoint is not a valid step.
         */
        public fun step(value: ClosedRange<KotlinInt>): FiktionConfigSettings<KotlinUIntProgression> =
            FiktionConfigSettings(listOf(minStep(value.start), maxStep(value.endInclusive), excludingSteps(emptyList())))

        /**
         * Creates settings that fix generated progression steps to [value] and clear configured step exclusions.
         *
         * Throws [FiktionConfigurationException] when [value] is not a valid step.
         */
        public fun step(value: KotlinInt): FiktionConfigSettings<KotlinUIntProgression> = step(value..value)

        /**
         * Samples two bounds from the composed candidate range and returns them as an ascending range.
         *
         * Throws [FiktionConfigurationException] when the composed bound candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun bounds(): KotlinUIntRange {
            val bounds = context.bounds(minBound, maxBound, excludingBounds)
            return bounds.start..bounds.endInclusive
        }

        /**
         * Samples a step from the composed step candidates after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed step candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun step(): KotlinInt = context.candidate(minStep, maxStep, excludingSteps)
    }

    /**
     * Unsigned long generator configuration.
     */
    public object ULong {
        /**
         * Minimum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val min: FiktionConfig<KotlinULong, KotlinULong> =
            FiktionConfig(KotlinULong.MIN_VALUE)

        /**
         * Maximum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val max: FiktionConfig<KotlinULong, KotlinULong> =
            FiktionConfig(KotlinULong.MAX_VALUE)

        /**
         * Candidate ranges excluded after min and max are composed.
         */
        public val excluding: FiktionConfig<KotlinULong, List<ClosedRange<KotlinULong>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace the candidate range endpoints with [value] and clear configured exclusions.
         */
        public fun range(value: ClosedRange<KotlinULong>): FiktionConfigSettings<KotlinULong> =
            FiktionConfigSettings(listOf(min(value.start), max(value.endInclusive), excluding(emptyList())))

        /**
         * Creates settings that fix the candidate range to [value] and clear configured exclusions.
         */
        public fun range(value: KotlinULong): FiktionConfigSettings<KotlinULong> = range(value..value)

        /**
         * Samples a value from the composed range after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun range(): KotlinULong = context.candidate(min, max, excluding)
    }

    /**
     * Unsigned long range generator configuration.
     */
    public object ULongRange {
        /**
         * Minimum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val minBound: FiktionConfig<KotlinULongRange, KotlinULong> =
            FiktionConfig(KotlinULong.MIN_VALUE)

        /**
         * Maximum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val maxBound: FiktionConfig<KotlinULongRange, KotlinULong> =
            FiktionConfig(KotlinULong.MAX_VALUE)

        /**
         * Candidate bounds excluded after minBound and maxBound are composed.
         */
        public val excludingBounds: FiktionConfig<KotlinULongRange, List<ClosedRange<KotlinULong>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated bound endpoints with [value] and clear configured bound exclusions.
         */
        public fun bounds(value: ClosedRange<KotlinULong>): FiktionConfigSettings<KotlinULongRange> =
            FiktionConfigSettings(listOf(minBound(value.start), maxBound(value.endInclusive), excludingBounds(emptyList())))

        /**
         * Creates settings that fix generated range bounds to [value] and clear configured bound exclusions.
         */
        public fun bounds(value: KotlinULong): FiktionConfigSettings<KotlinULongRange> = bounds(value..value)

        /**
         * Samples two bounds from the composed candidate range and returns them as an ascending range.
         *
         * Throws [FiktionConfigurationException] when the composed bound candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun bounds(): KotlinULongRange {
            val bounds = context.bounds(minBound, maxBound, excludingBounds)
            return bounds.start..bounds.endInclusive
        }
    }

    /**
     * Unsigned long progression generator configuration.
     */
    public object ULongProgression {
        /**
         * Minimum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val minBound: FiktionConfig<KotlinULongProgression, KotlinULong> =
            FiktionConfig(0uL)

        /**
         * Maximum candidate bound for generated ranges and progressions. The composed bound range is checked when candidates are sampled.
         */
        public val maxBound: FiktionConfig<KotlinULongProgression, KotlinULong> =
            FiktionConfig(100uL)

        /**
         * Minimum candidate step for generated progressions. The composed step range is checked when candidates are sampled.
         */
        public val minStep: FiktionConfig<KotlinULongProgression, KotlinLong> =
            FiktionConfig(1L, ::requirePositive)

        /**
         * Maximum candidate step for generated progressions. The composed step range is checked when candidates are sampled.
         */
        public val maxStep: FiktionConfig<KotlinULongProgression, KotlinLong> =
            FiktionConfig(5L, ::requirePositive)

        /**
         * Candidate bounds excluded after minBound and maxBound are composed.
         */
        public val excludingBounds: FiktionConfig<KotlinULongProgression, List<ClosedRange<KotlinULong>>> =
            exclusionsConfig()

        /**
         * Candidate steps excluded after minStep and maxStep are composed.
         */
        public val excludingSteps: FiktionConfig<KotlinULongProgression, List<ClosedRange<KotlinLong>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated bound endpoints with [value] and clear configured bound exclusions.
         */
        public fun bounds(value: ClosedRange<KotlinULong>): FiktionConfigSettings<KotlinULongProgression> =
            FiktionConfigSettings(listOf(minBound(value.start), maxBound(value.endInclusive), excludingBounds(emptyList())))

        /**
         * Creates settings that fix generated range bounds to [value] and clear configured bound exclusions.
         */
        public fun bounds(value: KotlinULong): FiktionConfigSettings<KotlinULongProgression> = bounds(value..value)

        /**
         * Creates settings that replace generated step endpoints with [value] and clear configured step exclusions.
         *
         * Throws [FiktionConfigurationException] when either endpoint is not a valid step.
         */
        public fun step(value: ClosedRange<KotlinLong>): FiktionConfigSettings<KotlinULongProgression> =
            FiktionConfigSettings(listOf(minStep(value.start), maxStep(value.endInclusive), excludingSteps(emptyList())))

        /**
         * Creates settings that fix generated progression steps to [value] and clear configured step exclusions.
         *
         * Throws [FiktionConfigurationException] when [value] is not a valid step.
         */
        public fun step(value: KotlinLong): FiktionConfigSettings<KotlinULongProgression> = step(value..value)

        /**
         * Samples two bounds from the composed candidate range and returns them as an ascending range.
         *
         * Throws [FiktionConfigurationException] when the composed bound candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun bounds(): KotlinULongRange {
            val bounds = context.bounds(minBound, maxBound, excludingBounds)
            return bounds.start..bounds.endInclusive
        }

        /**
         * Samples a step from the composed step candidates after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed step candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun step(): KotlinLong = context.candidate(minStep, maxStep, excludingSteps)
    }

    /**
     * Float generator configuration.
     */
    public object Float {
        /**
         * Minimum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val min: FiktionConfig<KotlinFloat, KotlinFloat> =
            FiktionConfig(0.0f)

        /**
         * Maximum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val max: FiktionConfig<KotlinFloat, KotlinFloat> =
            FiktionConfig(1.0f)

        /**
         * Candidate ranges excluded after min and max are composed.
         */
        public val excluding: FiktionConfig<KotlinFloat, List<ClosedRange<KotlinFloat>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace the candidate range endpoints with [value] and clear configured exclusions.
         */
        public fun range(value: ClosedRange<KotlinFloat>): FiktionConfigSettings<KotlinFloat> =
            FiktionConfigSettings(listOf(min(value.start), max(value.endInclusive), excluding(emptyList())))

        /**
         * Creates settings that fix the candidate range to [value] and clear configured exclusions.
         */
        public fun range(value: KotlinFloat): FiktionConfigSettings<KotlinFloat> = range(value..value)

        /**
         * Samples a value from the composed range after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun range(): KotlinFloat = context.candidates(min, max, excluding).sampleFloat(context.random)
    }

    /**
     * Double generator configuration.
     */
    public object Double {
        /**
         * Minimum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val min: FiktionConfig<KotlinDouble, KotlinDouble> =
            FiktionConfig(0.0)

        /**
         * Maximum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val max: FiktionConfig<KotlinDouble, KotlinDouble> =
            FiktionConfig(1.0)

        /**
         * Candidate ranges excluded after min and max are composed.
         */
        public val excluding: FiktionConfig<KotlinDouble, List<ClosedRange<KotlinDouble>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace the candidate range endpoints with [value] and clear configured exclusions.
         */
        public fun range(value: ClosedRange<KotlinDouble>): FiktionConfigSettings<KotlinDouble> =
            FiktionConfigSettings(listOf(min(value.start), max(value.endInclusive), excluding(emptyList())))

        /**
         * Creates settings that fix the candidate range to [value] and clear configured exclusions.
         */
        public fun range(value: KotlinDouble): FiktionConfigSettings<KotlinDouble> = range(value..value)

        /**
         * Samples a value from the composed range after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun range(): KotlinDouble = context.candidates(min, max, excluding).sampleDouble(context.random)
    }

    /**
     * Character generator configuration.
     */
    public object Char {
        /**
         * Charset used when generating text for this scope.
         */
        public val charset: FiktionConfig<KotlinChar, FiktionCharset> =
            FiktionConfig(FiktionCharset.AlphaNumeric)

        /**
         * Charsets used when generating character range bounds.
         */
        public val rangeCharsets: FiktionConfig<CharRange, List<FiktionCharset>> =
            FiktionConfig(
                listOf(FiktionCharset.LowercaseAlpha, FiktionCharset.UppercaseAlpha, FiktionCharset.Numeric),
            )

        /**
         * Minimum candidate step for generated progressions. The composed step range is checked when candidates are sampled.
         */
        public val minStep: FiktionConfig<KotlinCharProgression, KotlinInt> =
            FiktionConfig(1, ::requirePositive)

        /**
         * Maximum candidate step for generated progressions. The composed step range is checked when candidates are sampled.
         */
        public val maxStep: FiktionConfig<KotlinCharProgression, KotlinInt> =
            FiktionConfig(5, ::requirePositive)

        /**
         * Candidate steps excluded after minStep and maxStep are composed.
         */
        public val excludingSteps: FiktionConfig<KotlinCharProgression, List<ClosedRange<KotlinInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated step endpoints with [value] and clear configured step exclusions.
         *
         * Throws [FiktionConfigurationException] when either endpoint is not a valid step.
         */
        public fun step(value: ClosedRange<KotlinInt>): FiktionConfigSettings<KotlinCharProgression> =
            FiktionConfigSettings(listOf(minStep(value.start), maxStep(value.endInclusive), excludingSteps(emptyList())))

        /**
         * Creates settings that fix generated progression steps to [value] and clear configured step exclusions.
         *
         * Throws [FiktionConfigurationException] when [value] is not a valid step.
         */
        public fun step(value: KotlinInt): FiktionConfigSettings<KotlinCharProgression> = step(value..value)

        /**
         * Samples a step from the composed step candidates after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed step candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun step(): KotlinInt = context.candidate(minStep, maxStep, excludingSteps)
    }

    /**
     * String generator configuration.
     */
    public object String {
        /**
         * Minimum candidate length for generated text. The composed length range is checked when candidates are sampled.
         */
        public val minLength: FiktionConfig<KotlinString, KotlinInt> =
            FiktionConfig(1, ::requireNonNegative)

        /**
         * Maximum candidate length for generated text. The composed length range is checked when candidates are sampled.
         */
        public val maxLength: FiktionConfig<KotlinString, KotlinInt> =
            FiktionConfig(32, ::requireNonNegative)

        /**
         * Candidate lengths excluded after minLength and maxLength are composed.
         */
        public val excludingLengths: FiktionConfig<KotlinString, List<ClosedRange<KotlinInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated length endpoints with [value] and clear configured length exclusions.
         *
         * Throws [FiktionConfigurationException] when either endpoint is not a valid length.
         */
        public fun length(value: ClosedRange<KotlinInt>): FiktionConfigSettings<KotlinString> =
            FiktionConfigSettings(listOf(minLength(value.start), maxLength(value.endInclusive), excludingLengths(emptyList())))

        /**
         * Creates settings that fix generated lengths to [value] and clear configured length exclusions.
         *
         * Throws [FiktionConfigurationException] when [value] is not a valid length.
         */
        public fun length(value: KotlinInt): FiktionConfigSettings<KotlinString> = length(value..value)

        /**
         * Samples a length from the composed length candidates after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed length candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun length(): KotlinInt = context.candidate(minLength, maxLength, excludingLengths)

        /**
         * Charset used when generating text for this scope.
         */
        public val charset: FiktionConfig<KotlinString, FiktionCharset> =
            FiktionConfig(FiktionCharset.AlphaNumeric)
    }

    /**
     * Collection generator configuration.
     */
    public object Collection {
        /**
         * Minimum candidate size for generated containers. The composed size range is checked when candidates are sampled.
         */
        public val minSize: FiktionConfig<KotlinCollection<*>, KotlinInt> =
            FiktionConfig(0, ::requireNonNegative)

        /**
         * Maximum candidate size for generated containers. The composed size range is checked when candidates are sampled.
         */
        public val maxSize: FiktionConfig<KotlinCollection<*>, KotlinInt> =
            FiktionConfig(5, ::requireNonNegative)

        /**
         * Candidate sizes excluded after minSize and maxSize are composed.
         */
        public val excludingSizes: FiktionConfig<KotlinCollection<*>, List<ClosedRange<KotlinInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated size endpoints with [value] and clear configured size exclusions.
         *
         * Throws [FiktionConfigurationException] when either endpoint is not a valid size.
         */
        public fun size(value: ClosedRange<KotlinInt>): FiktionConfigSettings<KotlinCollection<*>> =
            FiktionConfigSettings(listOf(minSize(value.start), maxSize(value.endInclusive), excludingSizes(emptyList())))

        /**
         * Creates settings that fix generated sizes to [value] and clear configured size exclusions.
         *
         * Throws [FiktionConfigurationException] when [value] is not a valid size.
         */
        public fun size(value: KotlinInt): FiktionConfigSettings<KotlinCollection<*>> = size(value..value)

        /**
         * Samples a size from the composed size candidates after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed size candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun size(): KotlinInt = context.candidate(minSize, maxSize, excludingSizes)

        /**
         * Strategy used when generating set-like collections with distinct elements.
         */
        public val uniqueElementStrategy: FiktionConfig<KotlinCollection<*>, UniqueElementStrategy> =
            FiktionConfig(UniqueElementStrategy.BestEffort)
    }

    /**
     * Map generator configuration.
     */
    public object Map {
        /**
         * Minimum candidate size for generated containers. The composed size range is checked when candidates are sampled.
         */
        public val minSize: FiktionConfig<KotlinMap<*, *>, KotlinInt> =
            FiktionConfig(0, ::requireNonNegative)

        /**
         * Maximum candidate size for generated containers. The composed size range is checked when candidates are sampled.
         */
        public val maxSize: FiktionConfig<KotlinMap<*, *>, KotlinInt> =
            FiktionConfig(5, ::requireNonNegative)

        /**
         * Candidate sizes excluded after minSize and maxSize are composed.
         */
        public val excludingSizes: FiktionConfig<KotlinMap<*, *>, List<ClosedRange<KotlinInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated size endpoints with [value] and clear configured size exclusions.
         *
         * Throws [FiktionConfigurationException] when either endpoint is not a valid size.
         */
        public fun size(value: ClosedRange<KotlinInt>): FiktionConfigSettings<KotlinMap<*, *>> =
            FiktionConfigSettings(listOf(minSize(value.start), maxSize(value.endInclusive), excludingSizes(emptyList())))

        /**
         * Creates settings that fix generated sizes to [value] and clear configured size exclusions.
         *
         * Throws [FiktionConfigurationException] when [value] is not a valid size.
         */
        public fun size(value: KotlinInt): FiktionConfigSettings<KotlinMap<*, *>> = size(value..value)

        /**
         * Samples a size from the composed size candidates after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed size candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun size(): KotlinInt = context.candidate(minSize, maxSize, excludingSizes)
    }

    /**
     * Array generator configuration.
     */
    public object Array {
        /**
         * Minimum candidate size for generated containers. The composed size range is checked when candidates are sampled.
         */
        public val minSize: FiktionConfig<KotlinArray<*>, KotlinInt> =
            FiktionConfig(1, ::requireNonNegative)

        /**
         * Maximum candidate size for generated containers. The composed size range is checked when candidates are sampled.
         */
        public val maxSize: FiktionConfig<KotlinArray<*>, KotlinInt> =
            FiktionConfig(3, ::requireNonNegative)

        /**
         * Candidate sizes excluded after minSize and maxSize are composed.
         */
        public val excludingSizes: FiktionConfig<KotlinArray<*>, List<ClosedRange<KotlinInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated size endpoints with [value] and clear configured size exclusions.
         *
         * Throws [FiktionConfigurationException] when either endpoint is not a valid size.
         */
        public fun size(value: ClosedRange<KotlinInt>): FiktionConfigSettings<KotlinArray<*>> =
            FiktionConfigSettings(listOf(minSize(value.start), maxSize(value.endInclusive), excludingSizes(emptyList())))

        /**
         * Creates settings that fix generated sizes to [value] and clear configured size exclusions.
         *
         * Throws [FiktionConfigurationException] when [value] is not a valid size.
         */
        public fun size(value: KotlinInt): FiktionConfigSettings<KotlinArray<*>> = size(value..value)

        /**
         * Samples a size from the composed size candidates after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed size candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun size(): KotlinInt = context.candidate(minSize, maxSize, excludingSizes)
    }

    /**
     * Regex generator configuration.
     */
    public object Regex {
        /**
         * Minimum candidate length for generated text. The composed length range is checked when candidates are sampled.
         */
        public val minLength: FiktionConfig<kotlin.text.Regex, KotlinInt> =
            FiktionConfig(8, ::requireNonNegative)

        /**
         * Maximum candidate length for generated text. The composed length range is checked when candidates are sampled.
         */
        public val maxLength: FiktionConfig<kotlin.text.Regex, KotlinInt> =
            FiktionConfig(8, ::requireNonNegative)

        /**
         * Candidate lengths excluded after minLength and maxLength are composed.
         */
        public val excludingLengths: FiktionConfig<kotlin.text.Regex, List<ClosedRange<KotlinInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated length endpoints with [value] and clear configured length exclusions.
         *
         * Throws [FiktionConfigurationException] when either endpoint is not a valid length.
         */
        public fun length(value: ClosedRange<KotlinInt>): FiktionConfigSettings<kotlin.text.Regex> =
            FiktionConfigSettings(listOf(minLength(value.start), maxLength(value.endInclusive), excludingLengths(emptyList())))

        /**
         * Creates settings that fix generated lengths to [value] and clear configured length exclusions.
         *
         * Throws [FiktionConfigurationException] when [value] is not a valid length.
         */
        public fun length(value: KotlinInt): FiktionConfigSettings<kotlin.text.Regex> = length(value..value)

        /**
         * Samples a length from the composed length candidates after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed length candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun length(): KotlinInt = context.candidate(minLength, maxLength, excludingLengths)

        /**
         * Charset used when generating text for this scope.
         */
        public val charset: FiktionConfig<kotlin.text.Regex, FiktionCharset> =
            FiktionConfig(FiktionCharset.AlphaNumeric)
    }

    /**
     * Duration generator configuration.
     */
    public object Duration {
        /**
         * Minimum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val min: FiktionConfig<KotlinDuration, KotlinDuration> =
            FiktionConfig((-3_153_600_000_000L).milliseconds)

        /**
         * Maximum candidate value for generated values. The composed min/max range is checked when candidates are sampled.
         */
        public val max: FiktionConfig<KotlinDuration, KotlinDuration> =
            FiktionConfig(3_153_600_000_000L.milliseconds)

        /**
         * Candidate ranges excluded after min and max are composed.
         */
        public val excluding: FiktionConfig<KotlinDuration, List<ClosedRange<KotlinDuration>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace the candidate range endpoints with [value] and clear configured exclusions.
         */
        public fun range(value: ClosedRange<KotlinDuration>): FiktionConfigSettings<KotlinDuration> =
            FiktionConfigSettings(listOf(min(value.start), max(value.endInclusive), excluding(emptyList())))

        /**
         * Creates settings that fix the candidate range to [value] and clear configured exclusions.
         */
        public fun range(value: KotlinDuration): FiktionConfigSettings<KotlinDuration> = range(value..value)

        /**
         * Samples a value from the composed range after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun range(): KotlinDuration = context.candidate(min, max, excluding)
    }

    /**
     * Instant generator configuration.
     */
    public object Instant {
        /**
         * Minimum candidate epoch seconds for generated instants. The composed epoch-second range is checked when candidates are sampled.
         */
        public val minEpochSeconds: FiktionConfig<KotlinInstant, KotlinLong> =
            FiktionConfig(946_684_800L)

        /**
         * Maximum candidate epoch seconds for generated instants. The composed epoch-second range is checked when candidates are sampled.
         */
        public val maxEpochSeconds: FiktionConfig<KotlinInstant, KotlinLong> =
            FiktionConfig(4_102_444_799L)

        /**
         * Minimum candidate nanosecond adjustment for generated instants. The composed nanosecond range is checked when candidates are sampled.
         */
        public val minNanosecond: FiktionConfig<KotlinInstant, KotlinInt> =
            FiktionConfig(0, ::requireNonNegative)

        /**
         * Maximum candidate nanosecond adjustment for generated instants. The composed nanosecond range is checked when candidates are sampled.
         */
        public val maxNanosecond: FiktionConfig<KotlinInstant, KotlinInt> =
            FiktionConfig(999_999_999, ::requireNonNegative)

        /**
         * Candidate epoch seconds excluded after minEpochSeconds and maxEpochSeconds are composed.
         */
        public val excludingEpochSeconds: FiktionConfig<KotlinInstant, List<ClosedRange<KotlinLong>>> =
            exclusionsConfig()

        /**
         * Candidate nanoseconds excluded after minNanosecond and maxNanosecond are composed.
         */
        public val excludingNanoseconds: FiktionConfig<KotlinInstant, List<ClosedRange<KotlinInt>>> =
            exclusionsConfig()

        /**
         * Creates settings that replace generated epoch-second endpoints with [value] and clear configured exclusions.
         */
        public fun epochSeconds(value: ClosedRange<KotlinLong>): FiktionConfigSettings<KotlinInstant> =
            FiktionConfigSettings(
                listOf(minEpochSeconds(value.start), maxEpochSeconds(value.endInclusive), excludingEpochSeconds(emptyList())),
            )

        /**
         * Creates settings that fix generated epoch seconds to [value] and clear configured exclusions.
         */
        public fun epochSeconds(value: KotlinLong): FiktionConfigSettings<KotlinInstant> = epochSeconds(value..value)

        /**
         * Creates settings that replace generated nanosecond endpoints with [value] and clear configured exclusions.
         *
         * Throws [FiktionConfigurationException] when either endpoint is not a valid nanosecond.
         */
        public fun nanosecond(value: ClosedRange<KotlinInt>): FiktionConfigSettings<KotlinInstant> =
            FiktionConfigSettings(listOf(minNanosecond(value.start), maxNanosecond(value.endInclusive), excludingNanoseconds(emptyList())))

        /**
         * Creates settings that fix generated nanoseconds to [value] and clear configured exclusions.
         *
         * Throws [FiktionConfigurationException] when [value] is not a valid nanosecond.
         */
        public fun nanosecond(value: KotlinInt): FiktionConfigSettings<KotlinInstant> = nanosecond(value..value)

        /**
         * Samples epoch seconds from the composed candidates after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed epoch-second candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun epochSeconds(): KotlinLong = context.candidate(minEpochSeconds, maxEpochSeconds, excludingEpochSeconds)

        /**
         * Samples a nanosecond from the composed candidates after applying min, max, and exclusion settings.
         *
         * Throws [FiktionConfigurationException] when the composed nanosecond candidates are empty or too sparse.
         */
        context(context: FakeContext)
        public fun nanosecond(): KotlinInt = context.candidate(minNanosecond, maxNanosecond, excludingNanoseconds)
    }
}

/**
 * Creates a typed generator configuration value that fixes a closed-range configuration to [value].
 */
public operator fun <Scope> FiktionConfig<Scope, ClosedRange<KotlinInt>>.invoke(
    value: KotlinInt,
): FiktionConfigSetting<Scope, ClosedRange<KotlinInt>> = invoke(value..value)

/**
 * Creates a typed generator configuration value that fixes a closed-range configuration to [value].
 */
public operator fun <Scope> FiktionConfig<Scope, ClosedRange<KotlinLong>>.invoke(
    value: KotlinLong,
): FiktionConfigSetting<Scope, ClosedRange<KotlinLong>> = invoke(value..value)

/**
 * Creates a typed generator configuration value that fixes a closed-range configuration to [value].
 */
public operator fun <Scope> FiktionConfig<Scope, ClosedRange<KotlinFloat>>.invoke(
    value: KotlinFloat,
): FiktionConfigSetting<Scope, ClosedRange<KotlinFloat>> = invoke(value..value)

/**
 * Creates a typed generator configuration value that fixes a closed-range configuration to [value].
 */
public operator fun <Scope> FiktionConfig<Scope, ClosedRange<KotlinDouble>>.invoke(
    value: KotlinDouble,
): FiktionConfigSetting<Scope, ClosedRange<KotlinDouble>> = invoke(value..value)

/**
 * Creates a typed generator configuration value that fixes a closed-range configuration to [value].
 */
@JvmName("invokeUIntRange")
public operator fun <Scope> FiktionConfig<Scope, ClosedRange<KotlinUInt>>.invoke(
    value: KotlinUInt,
): FiktionConfigSetting<Scope, ClosedRange<KotlinUInt>> = invoke(value..value)

/**
 * Creates a typed generator configuration value that fixes a closed-range configuration to [value].
 */
@JvmName("invokeULongRange")
public operator fun <Scope> FiktionConfig<Scope, ClosedRange<KotlinULong>>.invoke(
    value: KotlinULong,
): FiktionConfigSetting<Scope, ClosedRange<KotlinULong>> = invoke(value..value)

/**
 * Typed generator configuration value.
 */
public data class FiktionConfigSetting<Scope, Value : Any>(
    /**
     * Configuration key this setting applies to.
     */
    public val key: FiktionConfig<Scope, Value>,
    /**
     * Configuration value registered for [key].
     */
    public val value: Value,
) : FiktionConfigSettingGroup<Scope> {
    override val settings: List<FiktionConfigSetting<*, *>> = listOf(this)
}

/**
 * Typed generator configuration values registered in declaration order.
 */
public sealed interface FiktionConfigSettingGroup<Scope> {
    /**
     * Configuration settings registered in declaration order.
     */
    public val settings: List<FiktionConfigSetting<*, *>>
}

/**
 * Multiple typed generator configuration values.
 */
public class FiktionConfigSettings<Scope> internal constructor(
    override val settings: List<FiktionConfigSetting<*, *>>,
) : FiktionConfigSettingGroup<Scope> {
    override fun toString(): KotlinString = "FiktionConfigSettings(settings=$settings)"
}

private fun <Scope, Value : Comparable<Value>> exclusionsConfig(): FiktionConfig<Scope, List<ClosedRange<Value>>> =
    FiktionConfig(emptyList()) { ranges ->
        ranges.forEach(::requireValidExclusionRange)
    }

private fun <Value : Comparable<Value>> requireValidExclusionRange(range: ClosedRange<Value>) {
    requireFiktionConfiguration(range.start <= range.endInclusive) {
        "exclusion range must not be empty."
    }
}

private fun requirePositive(value: KotlinInt) {
    requireFiktionConfiguration(value > 0) {
        "range start must be positive, but was $value."
    }
}

private fun requirePositive(value: KotlinLong) {
    requireFiktionConfiguration(value > 0) {
        "range start must be positive, but was $value."
    }
}

private fun requireNonNegative(value: KotlinInt) {
    requireFiktionConfiguration(value >= 0) {
        "range start must be non-negative, but was $value."
    }
}

/**
 * Strategy for generating distinct values for set-like collections.
 */
public sealed interface UniqueElementStrategy {
    /**
     * Generates the configured number of candidates and materializes them as a set.
     *
     * Duplicate candidates collapse, so the final set size can be smaller than the configured collection size.
     */
    public data object BestEffort : UniqueElementStrategy

    /**
     * Retries candidate generation until the requested number of distinct values is reached.
     *
     * Generation fails if fewer than the requested number of distinct values are produced after
     * `requestedSize * maxAttemptsPerElement` attempts.
     */
    public data class Exact(
        val maxAttemptsPerElement: KotlinInt = 16,
    ) : UniqueElementStrategy {
        init {
            require(maxAttemptsPerElement > 0) {
                "maxAttemptsPerElement must be positive, but was $maxAttemptsPerElement."
            }
        }
    }
}
