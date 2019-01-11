package iclaude.festivaleconomia2019.model


/*
    A session of the event (conference, meetup, etc.).
 */
data class Session(
    // required data
    val id: String,
    val title: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val location: String,
    val description: String,
    val tags1: List<String>, // type of session: session, codelab, officehour, meetup, etc.
    val tags2: List<String>, // topic of the session: Android, Economics, Cloud, Web, etc.
    val organizers: List<String>,
    // optional data
    val relatedSessions: List<String>?,
    val photoUrl: String?,
    val sessionUrl: String?,
    val youtubeUrl: String?
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

val Session.duration: Long
    //get() = endTime.time - startTime.time
    get() = 0
