package iclaude.festivaleconomia2019.model

import com.google.firebase.firestore.GeoPoint

/*
    Location of an event (session, conference, etc.).
 */

data class Location(
    val id: String = "",
    val name: String = "",
    val displayString: String? = null,
    val description: String = "",
    val coordinates: GeoPoint = GeoPoint(0.0, 0.0)
)
