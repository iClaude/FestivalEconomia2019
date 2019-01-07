package iclaude.festivaleconomia2019.model


import com.google.firebase.firestore.ServerTimestamp
import java.util.*


data class Event(
    val id: String,
    val title: String,
    @ServerTimestamp val startTime: Date,
    @ServerTimestamp val endTime: Date,
    val location: Location,
    val description: String,
    val tags1: List<String>,
    val tags2: List<String>,
    val organizers: List<String>,

    val relatedEvents: List<String>?,
    val photoUrl: String?,
    val sessionUrl: String?,
    val youtubeUrl: String?
)

fun Event.isLive(): Boolean {
    val now = System.currentTimeMillis()
    return startTime.time <= now && endTime.time >= now
}

fun Event.hasPhotoUrl(): Boolean = photoUrl?.isNotBlank() ?: false
fun Event.hasSessionUrl(): Boolean = sessionUrl?.isNotBlank() ?: false
fun Event.hasYoutubeUrl(): Boolean = youtubeUrl?.isNotBlank() ?: false
fun Event.hasRelatedEvents(): Boolean = relatedEvents?.isNotEmpty() ?: false

val Event.duration: Long
    get() = endTime.time - startTime.time