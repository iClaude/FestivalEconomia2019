package iclaude.festivaleconomia2019.ui.utils

import android.content.Context
import android.text.TextUtils
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.data_classes.Session
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit


fun zonedDateTimeFromTimestamp(context: Context?, timestamp: Long): ZonedDateTime =
    ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), getZoneId(context))

fun getEventTimeZone() = "Europe/Rome"

fun getZoneId(context: Context?): ZoneId {

    val showLocal = context?.getSharedPreferences(
        context.getString(R.string.shared_preferences_file),
        Context.MODE_PRIVATE
    )?.getBoolean(context.getString(R.string.pref_showlocal), false) ?: false
    return if (showLocal || TextUtils.isEmpty(getEventTimeZone()))
        ZoneId.systemDefault()
    else
        ZoneId.of(getEventTimeZone())
}

fun numberOfDays(context: Context?, sessions: List<Session>): Int {
    val startDate =
        zonedDateTimeFromTimestamp(context, sessions.first().startTimestamp)
    val endDate =
        zonedDateTimeFromTimestamp(context, sessions.last().startTimestamp)
    return Duration.between(startDate, endDate).toDays().toInt().plus(1)
}

fun daysLabels(context: Context?, sessions: List<Session>): MutableList<DayLabel> {
    val startDate =
        zonedDateTimeFromTimestamp(context, sessions.first().startTimestamp)
    val endDate =
        zonedDateTimeFromTimestamp(context, sessions.last().startTimestamp)

    val labels = mutableListOf<DayLabel>()

    var day: ZonedDateTime = startDate
    @Suppress("SpellCheckingInspection") val formatter = DateTimeFormatter.ofPattern("d MMMM")
    while (day <= endDate) {
        labels.add(DayLabel(day, day.format(formatter)))
        day = day.plusDays(1)
    }

    return labels
}

class DayLabel(val date: ZonedDateTime = ZonedDateTime.now(), val label: String)

fun sessionLength(context: Context, startTimestamp: Long, endTimestamp: Long): String {
    val startDate = zonedDateTimeFromTimestamp(context, startTimestamp)
    val endDate = zonedDateTimeFromTimestamp(context, endTimestamp)

    val diffHours = ChronoUnit.HOURS.between(startDate, endDate)
    val diffMinutes = ChronoUnit.MINUTES.between(startDate, endDate)

    return when (diffHours) {
        in 1..Long.MAX_VALUE -> "${context.resources.getQuantityString(
            R.plurals.time_hours,
            diffHours.toInt(),
            diffHours
        )}"
        else -> "${context.resources.getQuantityString(R.plurals.time_minutes, diffMinutes.toInt(), diffMinutes)}"
    }
}

fun startOfDay(day: ZonedDateTime): ZonedDateTime =
    ZonedDateTime.of(day.year, day.monthValue, day.dayOfMonth, 0, 0, 0, 0, day.zone)

fun endOfDay(day: ZonedDateTime): ZonedDateTime =
    ZonedDateTime.of(day.year, day.monthValue, day.dayOfMonth, 23, 59, 59, 0, day.zone)

fun timestampToZonedDateTime(timestamp: Long, context: Context?): ZonedDateTime =
    ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), getZoneId(context))

fun sessionInfoTimeDetails(context: Context?, startTimestamp: Long, endTimestamp: Long): String {
    val startTime = zonedDateTimeFromTimestamp(context, startTimestamp)
    val endTime = zonedDateTimeFromTimestamp(context, endTimestamp)

    val str1 = formatDate("EE, MMM d, h:mm - ", startTime)
    val str2 = formatDate("h:mm a", endTime)

    return str1 + str2
}

fun formatDate(format: String, time: ZonedDateTime): String {
    return DateTimeFormatter.ofPattern(format).format(time)
}

fun getDateShortStr(context: Context?, timestamp: Long): String {
    val time = zonedDateTimeFromTimestamp(context, timestamp)
    return formatDate("EE, MMM d", time)
}