package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

private const val DEFAULT_PROGRESSION_WIDTH = 100

/**
 * Generates a finite integer progression.
 */
public fun FakeContext.intProgression(): IntProgression =
    intRange(min = -DEFAULT_PROGRESSION_WIDTH, max = DEFAULT_PROGRESSION_WIDTH) step int(1..5)
