package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import kotlin.time.DurationUnit

/**
 * Generates a duration unit.
 */
public fun FakeContext.durationUnit(): DurationUnit {
    val entries = DurationUnit.entries
    return oneOf(entries.toList())
}
