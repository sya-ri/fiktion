package dev.s7a.fiktion

import kotlin.random.Random

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
     * Random instance derived from [seed].
     */
    public val random: Random
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
) : FakeContext {
    override val random: Random = Random(seed)
}
