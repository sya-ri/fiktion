package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.addon.kotlinx.datetime.KotlinxDatetimeFiktionConfig
import dev.s7a.fiktion.generators.int
import kotlinx.datetime.UtcOffset

/**
 * Generates a kotlinx-datetime UTC offset.
 */
public fun FakeContext.utcOffset(): UtcOffset = UtcOffset(hours = int(config(KotlinxDatetimeFiktionConfig.UtcOffset.hours)))
