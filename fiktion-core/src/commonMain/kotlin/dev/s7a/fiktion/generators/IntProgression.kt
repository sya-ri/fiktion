package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a finite integer progression.
 */
public fun FakeContext.intProgression(): IntProgression = FiktionConfig.IntProgression.bounds() step FiktionConfig.IntProgression.step()
