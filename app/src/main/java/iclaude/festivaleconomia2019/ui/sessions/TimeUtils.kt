package iclaude.festivaleconomia2019.ui.sessions

import android.content.Context
import android.text.TextUtils
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.data_classes.Session
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

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
    val startDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(sessions.first().startTimestamp), getZoneId(context))
    val endDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(sessions.last().startTimestamp), getZoneId(context))
    return Duration.between(startDate, endDate).toDays().toInt().plus(1)
}

fun daysLabels(context: Context?, sessions: List<Session>): MutableList<DayLabel> {
    val startDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(sessions.first().startTimestamp), getZoneId(context))
    val endDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(sessions.last().startTimestamp), getZoneId(context))

    val labels = mutableListOf<DayLabel>()

    var day: ZonedDateTime = startDate
    @Suppress("SpellCheckingInspection") val formatter = DateTimeFormatter.ofPattern("dd MMMM")
    while (day <= endDate) {
        labels.add(DayLabel(day, day.format(formatter)))
        day = day.plusDays(1)
    }

    return labels
}

class DayLabel(val date: ZonedDateTime?, val label: String)