@file:Suppress("DEPRECATION")

package dev.s7a.fiktion.addon.kotlinx.datetime

import dev.s7a.fiktion.CannotGenerateException
import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.invoke
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class KotlinxDatetimeFiktionAddonTest {
    @Test
    fun `kotlinx-datetime add-on rules are inactive until the add-on is installed`() {
        assertFailsWith<CannotGenerateException> {
            fake<TimeZone>(seed = 123)
        }
    }

    @Test
    fun `installed kotlinx-datetime add-on generates supported types`() {
        val fiktion =
            Fiktion {
                install(KotlinxDatetimeFiktionAddon)
            }

        assertTrue(fiktion.fake<Instant>(seed = 123).toEpochMilliseconds() in MIN_EPOCH_MILLISECONDS..MAX_EPOCH_MILLISECONDS)
        assertTrue(fiktion.fake<LocalDate>(seed = 123).year in 1900..2100)
        assertTrue(fiktion.fake<LocalTime>(seed = 123).hour in 0..23)
        assertTrue(fiktion.fake<LocalDateTime>(seed = 123).date.year in 1900..2100)
        assertTrue(fiktion.fake<TimeZone>(seed = 123).id.isNotBlank())
        assertTrue(fiktion.fake<UtcOffset>(seed = 123).totalSeconds in MIN_UTC_OFFSET_SECONDS..MAX_UTC_OFFSET_SECONDS)
        assertTrue(fiktion.fake<DatePeriod>(seed = 123).years in -200..200)
        assertTrue(fiktion.fake<DateTimePeriod>(seed = 123).years in -200..200)
        assertTrue(fiktion.fake<Month>(seed = 123) in Month.entries)
        assertTrue(fiktion.fake<DayOfWeek>(seed = 123) in DayOfWeek.entries)
    }

    @Test
    fun `installed kotlinx-datetime add-on uses configured ranges`() {
        val fiktion =
            Fiktion {
                install(KotlinxDatetimeFiktionAddon)
                this using KotlinxDatetimeFiktionConfig.LocalDate.year(2026..2026)
                this using KotlinxDatetimeFiktionConfig.LocalDate.month(5..5)
                this using KotlinxDatetimeFiktionConfig.LocalDate.day(31..31)
                this using KotlinxDatetimeFiktionConfig.LocalTime.hour(9..9)
                this using KotlinxDatetimeFiktionConfig.LocalTime.minute(30..30)
                this using KotlinxDatetimeFiktionConfig.LocalTime.second(0..0)
                this using KotlinxDatetimeFiktionConfig.LocalTime.nanosecond(123..123)
                this using KotlinxDatetimeFiktionConfig.UtcOffset.hours(9..9)
                this using KotlinxDatetimeFiktionConfig.DatePeriod.years(1..1)
                this using KotlinxDatetimeFiktionConfig.DateTimePeriod.years(0..0)
                this using KotlinxDatetimeFiktionConfig.DateTimePeriod.months(0..0)
                this using KotlinxDatetimeFiktionConfig.DateTimePeriod.days(0..0)
                this using KotlinxDatetimeFiktionConfig.DateTimePeriod.hours(0..0)
                this using KotlinxDatetimeFiktionConfig.DateTimePeriod.minutes(0..0)
                this using KotlinxDatetimeFiktionConfig.DateTimePeriod.seconds(0..0)
                this using KotlinxDatetimeFiktionConfig.DateTimePeriod.nanoseconds(7L..7L)
            }

        assertEquals(LocalDate(2026, 5, 31), fiktion.fake<LocalDate>(seed = 123))
        assertEquals(LocalTime(9, 30, 0, 123), fiktion.fake<LocalTime>(seed = 123))
        assertEquals(9 * 60 * 60, fiktion.fake<UtcOffset>(seed = 123).totalSeconds)
        assertEquals(1, fiktion.fake<DatePeriod>(seed = 123).years)
        assertEquals(7, fiktion.fake<DateTimePeriod>(seed = 123).nanoseconds)
    }
}

private const val MIN_EPOCH_MILLISECONDS: Long = -2_208_988_800_000L
private const val MAX_EPOCH_MILLISECONDS: Long = 4_102_444_799_999L
private const val MIN_UTC_OFFSET_SECONDS: Int = -18 * 60 * 60
private const val MAX_UTC_OFFSET_SECONDS: Int = 18 * 60 * 60
