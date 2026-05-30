package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import kotlinx.datetime.Month

/**
 * Generates a kotlinx-datetime month.
 */
public fun FakeContext.month(): Month = oneOf(Month.entries)
