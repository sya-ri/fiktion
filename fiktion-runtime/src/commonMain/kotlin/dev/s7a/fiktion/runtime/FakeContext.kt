package dev.s7a.fiktion.runtime

import kotlin.random.Random

/**
 * Runtime context passed to custom generators.
 */
public data class FakeContext
    @Deprecated(
        message = "FakeContext is provided by Fiktion. Use the context passed to generators instead.",
    )
    constructor(
        /**
         * Deterministic seed for the current generated value.
         */
        public val seed: Long,
        /**
         * Type currently being generated.
         */
        public val type: FakeType,
        /**
         * Property currently being generated, or `null` when generating a top-level value.
         */
        public val property: FakeProperty?,
        /**
         * Full path to the current generated value.
         */
        public val path: FakePath,
        /**
         * Current recursion depth.
         */
        public val depth: Int,
    ) {
        /**
         * Random instance derived from [seed].
         */
        public val random: Random = Random(seed)
    }
