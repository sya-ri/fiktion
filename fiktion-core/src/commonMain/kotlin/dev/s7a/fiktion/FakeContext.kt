package dev.s7a.fiktion

import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Runtime context passed to custom generators.
 */
public interface FakeContext {
    /**
     * Deterministic seed for the current generated value.
     */
    public val seed: Long

    /**
     * Type currently being generated.
     */
    public val type: FakeType

    /**
     * Property currently being generated, or `null` when generating a top-level value.
     */
    public val property: FakeProperty?

    /**
     * Full path to the current generated value.
     */
    public val path: FakePath

    /**
     * Current recursion depth.
     */
    public val depth: Int

    /**
     * Index of this value within the parent generator.
     */
    public val index: Int

    /**
     * Random instance derived from [seed].
     */
    public val random: Random

    /**
     * Returns the effective generator configuration value for [key].
     */
    public fun <Value : Any> config(key: FiktionConfig<*, Value>): Value = getConfig(key)
}

/**
 * Default runtime context implementation.
 */
internal data class DefaultFakeContext(
    override val seed: Long,
    override val type: FakeType,
    override val property: FakeProperty?,
    override val path: FakePath,
    override val depth: Int,
    override val index: Int,
    internal val configState: FiktionConfigState,
    internal val request: GenerationRequest,
) : FakeContext {
    override val random: Random = Random(seed)
}

internal fun <Value : Any> FakeContext.getConfig(key: FiktionConfig<*, Value>): Value =
    when (this) {
        is DefaultFakeContext -> configState.selectConfig(key = key, request = request)
        is TypeFamilyGenerationContext -> context.getConfig(key)
        else -> key.defaultValue
    }

internal fun <Scope, Value> FakeContext.candidate(
    min: FiktionConfig<Scope, Value>,
    max: FiktionConfig<Scope, Value>,
    excluding: FiktionConfig<Scope, List<ClosedRange<Value>>>,
): Value where Value : Any, Value : Comparable<Value> = candidates(min = min, max = max, excluding = excluding).sample(random)

internal fun <Scope, Value> FakeContext.bounds(
    min: FiktionConfig<Scope, Value>,
    max: FiktionConfig<Scope, Value>,
    excluding: FiktionConfig<Scope, List<ClosedRange<Value>>>,
): ClosedRange<Value> where Value : Any, Value : Comparable<Value> {
    val first = candidate(min = min, max = max, excluding = excluding)
    val last = candidate(min = min, max = max, excluding = excluding)
    return if (first <= last) first..last else last..first
}

internal fun <Scope, Value> FakeContext.candidates(
    min: FiktionConfig<Scope, Value>,
    max: FiktionConfig<Scope, Value>,
    excluding: FiktionConfig<Scope, List<ClosedRange<Value>>>,
): FiktionConfigCandidates<Value> where Value : Any, Value : Comparable<Value> =
    when (this) {
        is DefaultFakeContext -> {
            configState.selectCandidates(
                min = min,
                max = max,
                excluding = excluding,
                request = request,
            )
        }

        is TypeFamilyGenerationContext -> {
            context.candidates(min = min, max = max, excluding = excluding)
        }

        else -> {
            FiktionConfigCandidates(
                range = min.defaultValue..max.defaultValue,
                exclusions = excluding.defaultValue,
            ).also { candidates -> candidates.requireNotEmpty() }
        }
    }

internal fun <Scope, Value> FiktionConfigState.selectCandidates(
    min: FiktionConfig<Scope, Value>,
    max: FiktionConfig<Scope, Value>,
    excluding: FiktionConfig<Scope, List<ClosedRange<Value>>>,
    request: GenerationRequest,
): FiktionConfigCandidates<Value> where Value : Any, Value : Comparable<Value> {
    val start = selectConfig(key = min, request = request)
    val endInclusive = selectConfig(key = max, request = request)
    val exclusions = selectConfig(key = excluding, request = request)
    return FiktionConfigCandidates(range = start..endInclusive, exclusions = exclusions)
        .also { candidates -> candidates.requireNotEmpty() }
}

internal data class FiktionConfigCandidates<Value : Comparable<Value>>(
    val range: ClosedRange<Value>,
    val exclusions: List<ClosedRange<Value>>,
) {
    fun contains(value: Value): Boolean =
        range.start <= value &&
            value <= range.endInclusive &&
            exclusions.none { exclusion -> exclusion.start <= value && value <= exclusion.endInclusive }

    fun requireNotEmpty() {
        requireFiktionConfiguration(range.start <= range.endInclusive) {
            "candidate range must not be empty."
        }
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <Value : Comparable<Value>> FiktionConfigCandidates<Value>.sample(random: Random): Value =
    retryRandom {
        when (val start = range.start) {
            is Int -> {
                (start..range.endInclusive as Int).random(random)
            }

            is Long -> {
                (start..range.endInclusive as Long).randomCandidate(random)
            }

            is UInt -> {
                (start..range.endInclusive as UInt).random(random)
            }

            is ULong -> {
                (start..range.endInclusive as ULong).random(random)
            }

            is Float -> {
                start + random.nextUnitDouble().toFloat() * (range.endInclusive as Float - start)
            }

            is Double -> {
                start + random.nextUnitDouble() * (range.endInclusive as Double - start)
            }

            is Duration -> {
                (start.inWholeMilliseconds..(range.endInclusive as Duration).inWholeMilliseconds)
                    .randomCandidate(random)
                    .milliseconds
            }

            else -> {
                throw FiktionConfigurationException(
                    "candidate range for ${start::class} does not support random sampling.",
                )
            }
        } as Value
    }

internal fun FiktionConfigCandidates<Float>.sampleFloat(random: Random): Float =
    retryRandom {
        range.start + random.nextUnitDouble().toFloat() * (range.endInclusive - range.start)
    }

internal fun FiktionConfigCandidates<Double>.sampleDouble(random: Random): Double =
    retryRandom {
        range.start + random.nextUnitDouble() * (range.endInclusive - range.start)
    }

private fun UIntRange.random(random: Random): UInt {
    val size = last - first + 1u
    val value = random.nextInt().toUInt()
    return if (size == 0u) value else first + (value % size)
}

private fun Random.nextUnitDouble(): Double {
    val value = nextLong().mix().ushr(11)
    val max = Long.MAX_VALUE.ushr(11)
    return value.toDouble() / max.toDouble()
}

private fun Long.mix(): Long {
    var value = this + -7046029254386353131L
    value = (value xor value.ushr(30)) * -4658895280553007687L
    value = (value xor value.ushr(27)) * -7723592293110705685L
    return value xor value.ushr(31)
}

private fun LongRange.randomCandidate(random: Random): Long {
    val size = last - first + 1
    if (size > 0) {
        return first + (random.nextLong().toULong() % size.toULong()).toLong()
    }
    while (true) {
        val value = random.nextLong()
        if (value in this) return value
    }
}

private fun ULongRange.random(random: Random): ULong {
    val size = last - first + 1uL
    val value = random.nextLong().toULong()
    return if (size == 0uL) value else first + (value % size)
}

private inline fun <Value : Comparable<Value>> FiktionConfigCandidates<Value>.retryRandom(nextValue: () -> Value): Value {
    repeat(10_000) {
        val value = nextValue()
        if (contains(value)) return value
    }
    throw FiktionConfigurationException("candidate range is empty or too sparse after exclusions.")
}
