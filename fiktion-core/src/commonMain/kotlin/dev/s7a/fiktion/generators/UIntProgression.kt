package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

private const val DEFAULT_PROGRESSION_WIDTH = 100u

/**
 * Generates a finite unsigned integer progression.
 */
public fun FakeContext.uintProgression(): UIntProgression = uintRange(min = 0u, max = DEFAULT_PROGRESSION_WIDTH) step int(1..5)
