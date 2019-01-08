package iclaude.festivaleconomia2019.model


import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/*
    A session of the event (conference, meetup, etc.
 */
data class Session(
    // required data
    val id: String = "",
    val title: String = "",
    @ServerTimestamp val startTime: Date = Date(),
    @ServerTimestamp val endTime: Date = Date(),
    val location: Location = Location(),
    val description: String = "",
    val tags1: List<String> = emptyList(), // type of session: session, codelab, officehour, meetup, etc.
    val tags2: List<String> = emptyList(), // topic of the session: Android, Economics, Cloud, Web, etc.
    val organizers: List<Organizer> = emptyList(),
    // optional data
    val relatedSessions: List<String>? = null,
    val photoUrl: String? = null,
    val sessionUrl: String? = null,
    val youtubeUrl: String? = null,
    val starringUsers: List<String>? = null // list of uids of users who starred this session
)

fun Session.isLive(): Boolean {
    val now = System.currentTimeMillis()
    return true
    //return startTime?.time <= now && endTime?.time >= now
}

fun Session.hasPhotoUrl() = photoUrl?.isNotBlank() ?: false
fun Session.hasSessionUrl() = sessionUrl?.isNotBlank() ?: false
fun Session.hasYoutubeUrl() = youtubeUrl?.isNotBlank() ?: false
fun Session.hasRelatedEvents() = relatedSessions?.isNotEmpty() ?: false
fun Session.isStarredByUser(userUid: String) = starringUsers?.contains(userUid) ?: false

val Session.duration: Long
    //get() = endTime.time - startTime.time
    get() = 0
