package database

import kotlin.time.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()

fun getTodayDateString(): String {
    return Clock.System.now().toLocalDateTime(TimeZone.UTC).date.toString()
}

fun minusDays(dateString: String, days: Int): String {
    return try {
        val date = LocalDate.parse(dateString)
        date.minus(DatePeriod(days = days)).toString()
    } catch (_: Exception) {
        "1995-06-16"
    }
}
