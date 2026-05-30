@file:Suppress("DEPRECATION")

package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.addon.kotlinx.datetime.KotlinxDatetimeFiktionConfig
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.long
import kotlinx.datetime.Instant

/**
 * Generates a kotlinx-datetime instant from 1900-01-01T00:00:00Z until 2101-01-01T00:00:00Z.
 */
@Deprecated("Use the kotlin.time.Instant generator from fiktion-core.")
public fun FakeContext.instant(): Instant =
    Instant.fromEpochSeconds(
        long(config(KotlinxDatetimeFiktionConfig.Instant.epochSeconds)),
        int(config(KotlinxDatetimeFiktionConfig.Instant.nanosecond)),
    )
