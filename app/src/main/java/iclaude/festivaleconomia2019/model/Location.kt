package iclaude.festivaleconomia2019.model

/*
    Location of an event (session, conference, etc.).
 */

data class Location(
    val id: String,
    val name: String,
    val coordinates: Coordinates
)

data class Coordinates(val latitude: Double, val longitude: Double)