package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a finite unsigned long progression.
 */
public fun FakeContext.ulongProgression(): ULongProgression =
    FiktionConfig.ULongProgression.bounds() step FiktionConfig.ULongProgression.step()
