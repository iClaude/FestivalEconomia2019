package iclaude.festivaleconomia2019.model

import com.google.firebase.firestore.GeoPoint

/*
    Location of an event (session, conference, etc.).
 */

data class Location(
    val name: String,
    val details: String,
    val coordinates: GeoPoint
)
