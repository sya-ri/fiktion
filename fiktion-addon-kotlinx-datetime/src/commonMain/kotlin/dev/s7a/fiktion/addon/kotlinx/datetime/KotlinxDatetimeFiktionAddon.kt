@file:Suppress("DEPRECATION")

package dev.s7a.fiktion.addon.kotlinx.datetime

import dev.s7a.fiktion.FiktionAddon
import dev.s7a.fiktion.FiktionAddonBuilder
import dev.s7a.fiktion.addon.kotlinx.datetime.generators.datePeriod
import dev.s7a.fiktion.addon.kotlinx.datetime.generators.dateTimePeriod
import dev.s7a.fiktion.addon.kotlinx.datetime.generators.dayOfWeek
import dev.s7a.fiktion.addon.kotlinx.datetime.generators.instant
import dev.s7a.fiktion.addon.kotlinx.datetime.generators.localDate
import dev.s7a.fiktion.addon.kotlinx.datetime.generators.localDateTime
import dev.s7a.fiktion.addon.kotlinx.datetime.generators.localTime
import dev.s7a.fiktion.addon.kotlinx.datetime.generators.month
import dev.s7a.fiktion.addon.kotlinx.datetime.generators.timeZone
import dev.s7a.fiktion.addon.kotlinx.datetime.generators.utcOffset
import dev.s7a.fiktion.generatesBy
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

/**
 * Fiktion add-on that contributes generation rules for kotlinx-datetime types.
 */
public object KotlinxDatetimeFiktionAddon : FiktionAddon {
    override val id: String = "kotlinx-datetime"

    override fun install(builder: FiktionAddonBuilder) {
        with(builder) {
            type<Instant>() generatesBy {
                instant()
            }
            type<LocalDate>() generatesBy {
                localDate()
            }
            type<LocalTime>() generatesBy {
                localTime()
            }
            type<LocalDateTime>() generatesBy {
                localDateTime()
            }
            type<TimeZone>() generatesBy {
                timeZone()
            }
            type<UtcOffset>() generatesBy {
                utcOffset()
            }
            type<DatePeriod>() generatesBy {
                datePeriod()
            }
            type<DateTimePeriod>() generatesBy {
                dateTimePeriod()
            }
            type<Month>() generatesBy {
                month()
            }
            type<DayOfWeek>() generatesBy {
                dayOfWeek()
            }
        }
    }
}
