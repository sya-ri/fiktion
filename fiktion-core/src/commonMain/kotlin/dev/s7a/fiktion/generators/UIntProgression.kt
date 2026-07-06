package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a finite unsigned integer progression.
 */
public fun FakeContext.uintProgression(): UIntProgression = FiktionConfig.UIntProgression.bounds() step FiktionConfig.UIntProgression.step()
