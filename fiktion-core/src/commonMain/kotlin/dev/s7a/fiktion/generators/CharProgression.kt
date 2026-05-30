package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a finite character progression.
 */
public fun FakeContext.charProgression(): CharProgression = charRange() step int(config(FiktionConfig.Char.step))
