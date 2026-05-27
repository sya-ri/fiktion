package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.FakeContext
import dev.s7a.fiktion.runtime.FakePath
import dev.s7a.fiktion.runtime.FakeProperty
import dev.s7a.fiktion.runtime.FakeType
import kotlin.random.Random

/**
 * Default generator context passed to custom generators.
 */
internal data class DefaultFakeContext(
    /**
     * Seed used for this generation request.
     */
    override val seed: Long,
    /**
     * Random instance derived from [seed].
     */
    override val random: Random,
    /**
     * Runtime metadata for the generated type.
     */
    override val type: FakeType,
    /**
     * Runtime metadata for the generated property, if any.
     */
    override val property: FakeProperty?,
    /**
     * Runtime metadata for the generated property path.
     */
    override val path: FakePath,
    /**
     * Current object-graph depth.
     */
    override val depth: Int,
) : FakeContext
