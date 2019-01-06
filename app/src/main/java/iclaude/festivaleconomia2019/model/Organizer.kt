package iclaude.festivaleconomia2019.model

/*
    Organizer of the event (speaker, leader, etc.).
 */

data class Organizer(
    // required data
    val id: String,
    val name: String,
    val bio: String,
    // optional data
    val company: String?,
    val imageUrl: String?,
    val websiteUrl: String?,
    val twitterUrl: String?,
    val linkedInUrl: String?,
    val facebookUrl: String?
)

fun Organizer.hasCompany(): Boolean = company?.isNotBlank() ?: false
fun Organizer.hasImageUrl(): Boolean = imageUrl?.isNotBlank() ?: false
fun Organizer.hasWebsiteUrl(): Boolean = websiteUrl?.isNotBlank() ?: false
fun Organizer.hastwitterUrl(): Boolean = twitterUrl?.isNotBlank() ?: false
fun Organizer.haslinkedInUrl(): Boolean = linkedInUrl?.isNotBlank() ?: false
fun Organizer.hasfacebookUrl(): Boolean = facebookUrl?.isNotBlank() ?: false