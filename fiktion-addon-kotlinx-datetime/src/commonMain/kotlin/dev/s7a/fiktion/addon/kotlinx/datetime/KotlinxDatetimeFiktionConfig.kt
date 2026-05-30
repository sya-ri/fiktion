@file:Suppress("DEPRECATION")

package dev.s7a.fiktion.addon.kotlinx.datetime

import dev.s7a.fiktion.FiktionConfig
import kotlin.ranges.ClosedRange
import kotlinx.datetime.DatePeriod as KotlinxDatePeriod
import kotlinx.datetime.DateTimePeriod as KotlinxDateTimePeriod
import kotlinx.datetime.Instant as KotlinxInstant
import kotlinx.datetime.LocalDate as KotlinxLocalDate
import kotlinx.datetime.LocalTime as KotlinxLocalTime
import kotlinx.datetime.UtcOffset as KotlinxUtcOffset

/**
 * Typed generator configuration keys for the kotlinx-datetime add-on.
 */
public object KotlinxDatetimeFiktionConfig {
    /**
     * kotlinx-datetime instant generator configuration.
     */
    public object Instant {
        public val epochSeconds: FiktionConfig<KotlinxInstant, ClosedRange<Long>> =
            FiktionConfig(MIN_EPOCH_SECOND until MAX_EPOCH_SECOND_EXCLUSIVE)

        public val nanosecond: FiktionConfig<KotlinxInstant, ClosedRange<Int>> =
            FiktionConfig(0 until MAX_NANOSECOND_EXCLUSIVE)
    }

    /**
     * kotlinx-datetime local date generator configuration.
     */
    public object LocalDate {
        public val year: FiktionConfig<KotlinxLocalDate, ClosedRange<Int>> =
            FiktionConfig(1900..2100)

        public val month: FiktionConfig<KotlinxLocalDate, ClosedRange<Int>> =
            FiktionConfig(1..12)

        public val day: FiktionConfig<KotlinxLocalDate, ClosedRange<Int>> =
            FiktionConfig(1..28)
    }

    /**
     * kotlinx-datetime local time generator configuration.
     */
    public object LocalTime {
        public val hour: FiktionConfig<KotlinxLocalTime, ClosedRange<Int>> =
            FiktionConfig(0..23)

        public val minute: FiktionConfig<KotlinxLocalTime, ClosedRange<Int>> =
            FiktionConfig(0..59)

        public val second: FiktionConfig<KotlinxLocalTime, ClosedRange<Int>> =
            FiktionConfig(0..59)

        public val nanosecond: FiktionConfig<KotlinxLocalTime, ClosedRange<Int>> =
            FiktionConfig(0 until MAX_NANOSECOND_EXCLUSIVE)
    }

    /**
     * kotlinx-datetime UTC offset generator configuration.
     */
    public object UtcOffset {
        public val hours: FiktionConfig<KotlinxUtcOffset, ClosedRange<Int>> =
            FiktionConfig(-18..18)
    }

    /**
     * kotlinx-datetime date period generator configuration.
     */
    public object DatePeriod {
        public val years: FiktionConfig<KotlinxDatePeriod, ClosedRange<Int>> =
            FiktionConfig(-200..200)

        public val months: FiktionConfig<KotlinxDatePeriod, ClosedRange<Int>> =
            FiktionConfig(-24..24)

        public val days: FiktionConfig<KotlinxDatePeriod, ClosedRange<Int>> =
            FiktionConfig(-366..366)
    }

    /**
     * kotlinx-datetime date-time period generator configuration.
     */
    public object DateTimePeriod {
        public val years: FiktionConfig<KotlinxDateTimePeriod, ClosedRange<Int>> =
            FiktionConfig(-200..200)

        public val months: FiktionConfig<KotlinxDateTimePeriod, ClosedRange<Int>> =
            FiktionConfig(-24..24)

        public val days: FiktionConfig<KotlinxDateTimePeriod, ClosedRange<Int>> =
            FiktionConfig(-366..366)

        public val hours: FiktionConfig<KotlinxDateTimePeriod, ClosedRange<Int>> =
            FiktionConfig(-48..48)

        public val minutes: FiktionConfig<KotlinxDateTimePeriod, ClosedRange<Int>> =
            FiktionConfig(-120..120)

        public val seconds: FiktionConfig<KotlinxDateTimePeriod, ClosedRange<Int>> =
            FiktionConfig(-120..120)

        public val nanoseconds: FiktionConfig<KotlinxDateTimePeriod, ClosedRange<Long>> =
            FiktionConfig(-999_999_999L..999_999_999L)
    }
}

private const val MIN_EPOCH_SECOND: Long = -2_208_988_800L
private const val MAX_EPOCH_SECOND_EXCLUSIVE: Long = 4_134_844_800L
private const val MAX_NANOSECOND_EXCLUSIVE: Int = 1_000_000_000
