package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import kotlin.time.Duration

/**
 * Generates a finite duration within approximately plus or minus 100 years.
 */
public fun FakeContext.duration(): Duration = FiktionConfig.Duration.range()
