package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a finite long progression.
 */
public fun FakeContext.longProgression(): LongProgression = FiktionConfig.LongProgression.bounds() step FiktionConfig.LongProgression.step()
