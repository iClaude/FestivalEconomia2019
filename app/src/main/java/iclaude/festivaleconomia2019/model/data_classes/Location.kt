package iclaude.festivaleconomia2019.model.data_classes

/*
    Location of an event (session, conference, etc.).
 */

data class Location(
    val id: String,
    val name: String,
    val displayString: String?,
    val description: String,
    val lat: Double,
    val lng: Double
)
