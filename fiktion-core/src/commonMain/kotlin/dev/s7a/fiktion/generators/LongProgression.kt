package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

private const val DEFAULT_PROGRESSION_WIDTH = 100L

/**
 * Generates a finite long progression.
 */
public fun FakeContext.longProgression(): LongProgression =
    longRange(min = -DEFAULT_PROGRESSION_WIDTH, max = DEFAULT_PROGRESSION_WIDTH) step long(1L..5L)
