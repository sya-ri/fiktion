@file:Suppress("DEPRECATION")

package dev.s7a.fiktion.addon.kotlinx.datetime

import dev.s7a.fiktion.CannotGenerateException
import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
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
}

private const val MIN_EPOCH_MILLISECONDS: Long = -2_208_988_800_000L
private const val MAX_EPOCH_MILLISECONDS: Long = 4_102_444_799_999L
private const val MIN_UTC_OFFSET_SECONDS: Int = -18 * 60 * 60
private const val MAX_UTC_OFFSET_SECONDS: Int = 18 * 60 * 60
