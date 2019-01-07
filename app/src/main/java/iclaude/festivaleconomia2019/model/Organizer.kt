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
    val imageUrl: String? = null,
    val websiteUrl: String? = null,
    val twitterUrl: String? = null,
    val linkedInUrl: String? = null,
    val facebookUrl: String? = null
)

fun Organizer.hasCompany(): Boolean = company?.isNotBlank() ?: false
fun Organizer.hasImageUrl(): Boolean = imageUrl?.isNotBlank() ?: false
fun Organizer.hasWebsiteUrl(): Boolean = websiteUrl?.isNotBlank() ?: false
fun Organizer.hastwitterUrl(): Boolean = twitterUrl?.isNotBlank() ?: false
fun Organizer.haslinkedInUrl(): Boolean = linkedInUrl?.isNotBlank() ?: false
fun Organizer.hasfacebookUrl(): Boolean = facebookUrl?.isNotBlank() ?: false