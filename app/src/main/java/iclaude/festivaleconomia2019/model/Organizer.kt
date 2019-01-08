package iclaude.festivaleconomia2019.model

/*
    Organizer of the event (speaker, leader, etc.).
 */

data class Organizer(
    // required data
    val id: String = "",
    val name: String = "",
    val bio: String = "",
    val sessions: List<String> = emptyList(), // list of ids of Session(s) belonging to this Organizer
    // optional data
    val company: String? = null,
    val photoUrl: String? = null,
    val websiteUrl: String? = null,
    val twitterUrl: String? = null,
    val linkedInUrl: String? = null,
    val facebookUrl: String? = null
)

fun Organizer.hasCompany() = company?.isNotBlank() ?: false
fun Organizer.hasPhotoUrl() = photoUrl?.isNotBlank() ?: false
fun Organizer.hasWebsiteUrl() = websiteUrl?.isNotBlank() ?: false
fun Organizer.hastwitterUrl() = twitterUrl?.isNotBlank() ?: false
fun Organizer.haslinkedInUrl() = linkedInUrl?.isNotBlank() ?: false
fun Organizer.hasfacebookUrl() = facebookUrl?.isNotBlank() ?: false