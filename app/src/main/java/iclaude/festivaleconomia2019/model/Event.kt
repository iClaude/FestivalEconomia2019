package iclaude.festivaleconomia2019.model


import com.google.firebase.firestore.ServerTimestamp
import java.util.*


data class Event(
    val id: String = "",
    val title: String = "",
    @ServerTimestamp val startTime: Date = Date(),
    @ServerTimestamp val endTime: Date = Date(),
    val location: Location = Location(),
    val description: String = "",
    val tags1: List<String> = emptyList(),
    val tags2: List<String> = emptyList(),
    val organizers: List<String> = emptyList(),

    val relatedEvents: List<String>? = null,
    val photoUrl: String? = null,
    val sessionUrl: String? = null,
    val youtubeUrl: String? = null
)

fun Event.isLive(): Boolean {
    val now = System.currentTimeMillis()
    return true
    //return startTime?.time <= now && endTime?.time >= now
}

fun Event.hasPhotoUrl(): Boolean = photoUrl?.isNotBlank() ?: false
fun Event.hasSessionUrl(): Boolean = sessionUrl?.isNotBlank() ?: false
fun Event.hasYoutubeUrl(): Boolean = youtubeUrl?.isNotBlank() ?: false
fun Event.hasRelatedEvents(): Boolean = relatedEvents?.isNotEmpty() ?: false

val Event.duration: Long
    //get() = endTime.time - startTime.time
    get() = 0