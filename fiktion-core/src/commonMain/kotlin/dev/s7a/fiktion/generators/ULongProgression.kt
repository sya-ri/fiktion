package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

private const val DEFAULT_PROGRESSION_WIDTH = 100uL

/**
 * Generates a finite unsigned long progression.
 */
public fun FakeContext.ulongProgression(): ULongProgression =
    ulongRange(min = 0uL, max = DEFAULT_PROGRESSION_WIDTH) step long(1L..5L)
