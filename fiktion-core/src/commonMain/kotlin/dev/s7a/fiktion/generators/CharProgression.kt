package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a finite character progression.
 */
public fun FakeContext.charProgression(): CharProgression = charRange() step int(1..5)
