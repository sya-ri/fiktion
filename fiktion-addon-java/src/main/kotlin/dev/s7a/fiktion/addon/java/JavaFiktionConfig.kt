package dev.s7a.fiktion.addon.java

import dev.s7a.fiktion.FiktionConfig
import kotlin.ranges.ClosedRange
import java.time.Duration as JavaDuration
import java.time.Instant as JavaInstant
import java.time.LocalDate as JavaLocalDate
import java.time.LocalTime as JavaLocalTime
import java.time.ZoneOffset as JavaZoneOffset

/**
 * Typed generator configuration keys for the Java add-on.
 */
public object JavaFiktionConfig {
    /**
     * Java instant generator configuration.
     */
    public object Instant {
        public val epochSeconds: FiktionConfig<JavaInstant, ClosedRange<Long>> =
            FiktionConfig(MIN_EPOCH_SECOND until MAX_EPOCH_SECOND_EXCLUSIVE)

        public val nanosecond: FiktionConfig<JavaInstant, ClosedRange<Int>> =
            FiktionConfig(0 until MAX_NANOSECOND_EXCLUSIVE)
    }

    /**
     * Java local date generator configuration.
     */
    public object LocalDate {
        public val epochDays: FiktionConfig<JavaLocalDate, ClosedRange<Long>> =
            FiktionConfig(MIN_EPOCH_DAY until MAX_EPOCH_DAY_EXCLUSIVE)
    }

    /**
     * Java local time generator configuration.
     */
    public object LocalTime {
        public val nanosecondsOfDay: FiktionConfig<JavaLocalTime, ClosedRange<Long>> =
            FiktionConfig(0 until NANOSECONDS_PER_DAY)
    }

    /**
     * Java zone offset generator configuration.
     */
    public object ZoneOffset {
        public val hours: FiktionConfig<JavaZoneOffset, ClosedRange<Int>> =
            FiktionConfig(-12..14)
    }

    /**
     * Java duration generator configuration.
     */
    public object Duration {
        public val millis: FiktionConfig<JavaDuration, ClosedRange<Long>> =
            FiktionConfig(-MAX_DURATION_MILLISECONDS..MAX_DURATION_MILLISECONDS)
    }
}

private val MIN_EPOCH_SECOND: Long = JavaInstant.parse("1900-01-01T00:00:00Z").epochSecond
private val MAX_EPOCH_SECOND_EXCLUSIVE: Long = JavaInstant.parse("2101-01-01T00:00:00Z").epochSecond
private val MIN_EPOCH_DAY: Long = JavaLocalDate.of(1900, 1, 1).toEpochDay()
private val MAX_EPOCH_DAY_EXCLUSIVE: Long = JavaLocalDate.of(2101, 1, 1).toEpochDay()
private const val MAX_NANOSECOND_EXCLUSIVE: Int = 1_000_000_000
private const val NANOSECONDS_PER_DAY: Long = 86_400_000_000_000L
private const val MAX_DURATION_MILLISECONDS: Long = 3_153_600_000_000L
