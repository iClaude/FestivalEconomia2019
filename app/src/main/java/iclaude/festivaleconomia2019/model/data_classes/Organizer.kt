package iclaude.festivaleconomia2019.model.data_classes

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
    val thumbnailUrl: String?,
    val websiteUrl: String?,
    val twitterUrl: String?,
    val linkedInUrl: String?,
    val facebookUrl: String?
)

fun Organizer.hasCompany() = company?.isNotBlank() ?: false
fun Organizer.hasThumbnailUrl() = thumbnailUrl?.isNotBlank() ?: false
fun Organizer.hasWebsiteUrl() = websiteUrl?.isNotBlank() ?: false
fun Organizer.hastwitterUrl() = twitterUrl?.isNotBlank() ?: false
fun Organizer.haslinkedInUrl() = linkedInUrl?.isNotBlank() ?: false
fun Organizer.hasfacebookUrl() = facebookUrl?.isNotBlank() ?: false