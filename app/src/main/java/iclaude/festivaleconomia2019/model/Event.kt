package iclaude.festivaleconomia2019.model

import org.threeten.bp.ZonedDateTime


data class Event(
    val id: String,
    val title: String,
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime,
    val location: Location,
    val description: String,
    val tags: List<Tag>,
    val organizers: List<Organizer>,

    val relatedEvents: Set<Event>?,
    val photoUrl: String?,
    val sessionUrl: String?,
    val youtubeUrl: String?
)

fun Event.isLive(): Boolean {
    val now = ZonedDateTime.now()
    return startTime <= now && endTime >= now
}

fun Event.hasPhotoUrl(): Boolean = photoUrl?.isNotBlank() ?: false
fun Event.hasSessionUrl(): Boolean = sessionUrl?.isNotBlank() ?: false
fun Event.hasYoutubeUrl(): Boolean = youtubeUrl?.isNotBlank() ?: false
fun Event.hasRelatedEvents(): Boolean = relatedEvents?.isNotEmpty() ?: false

val Event.duration: Long
    get() = endTime.toInstant().toEpochMilli() - startTime.toInstant().toEpochMilli()