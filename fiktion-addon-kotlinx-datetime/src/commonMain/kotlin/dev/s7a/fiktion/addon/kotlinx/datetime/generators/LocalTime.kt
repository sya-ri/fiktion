package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.addon.kotlinx.datetime.KotlinxDatetimeFiktionConfig
import dev.s7a.fiktion.generators.int
import kotlinx.datetime.LocalTime

/**
 * Generates a kotlinx-datetime local time.
 */
public fun FakeContext.localTime(): LocalTime =
    LocalTime(
        hour = int(config(KotlinxDatetimeFiktionConfig.LocalTime.hour)),
        minute = int(config(KotlinxDatetimeFiktionConfig.LocalTime.minute)),
        second = int(config(KotlinxDatetimeFiktionConfig.LocalTime.second)),
        nanosecond = int(config(KotlinxDatetimeFiktionConfig.LocalTime.nanosecond)),
    )
