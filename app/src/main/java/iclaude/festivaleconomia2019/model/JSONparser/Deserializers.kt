package iclaude.festivaleconomia2019.model.JSONparser

import android.graphics.Color.parseColor
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import iclaude.festivaleconomia2019.model.data_classes.Location
import iclaude.festivaleconomia2019.model.data_classes.Organizer
import iclaude.festivaleconomia2019.model.data_classes.Session
import iclaude.festivaleconomia2019.model.data_classes.Tag
import java.lang.reflect.Type


class LocationDeserializer : JsonDeserializer<Location> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Location {
        val obj = json?.asJsonObject!!

        return Location(
                id = obj.get("id").asString,
                name = obj.get("name").asString,
                displayString = obj.get("displayString")?.asString,
                description = obj.get("description").asString,
                lat = obj.get("lat").asDouble,
                lng = obj.get("lng").asDouble

        )
    }
}

class SessionDeserializer : JsonDeserializer<Session> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Session {
        val obj = json?.asJsonObject!!

        val tags1 = getListFromJsonArray(obj, "tags1")
        val tags2 = getListFromJsonArray(obj, "tags2")
        val organizers = getListFromJsonArray(obj, "organizers")
        val relatedSessions = getListFromJsonArray(obj, "relatedSessions")

        return Session(
                id = obj.get("id").asString,
                title = obj.get("title").asString,
                startTimestamp = obj.get("startTimestamp").asLong,
                endTimestamp = obj.get("endTimestamp").asLong,
                location = obj.get("location").asString,
                description = obj.get("description").asString,
                tags1 = tags1.toList(),
                tags2 = tags2.toList(),
                organizers = organizers.toList(),
                relatedSessions = relatedSessions.toList(),
                photoUrl = obj.get("photoUrl")?.asString,
                sessionUrl = obj.get("sessionUrl")?.asString,
                youtubeUrl = obj.get("youtubeUrl")?.asString
        )
    }

    private fun getListFromJsonArray(obj: JsonObject, key: String): List<String> {
        val array = obj.get(key).asJsonArray
        val stringList = ArrayList<String>()
        array.mapTo(stringList) { it.asString }
        return stringList
    }
}

class OrganizerDeserializer : JsonDeserializer<Organizer> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Organizer {
        val obj = json?.asJsonObject!!

        return Organizer(
                id = obj.get("id").asString,
                name = obj.get("name").asString,
                bio = obj.get("bio").asString,
                company = obj.get("company")?.asString,
                thumbnailUrl = obj.get("thumbnailUrl")?.asString,
                websiteUrl = obj.get("websiteUrl")?.asString,
                twitterUrl = obj.get("twitterUrl")?.asString,
                linkedInUrl = obj.get("linkedInUrl")?.asString,
                facebookUrl = obj.get("facebookUrl")?.asString
        )
    }
}

class TagDeserializer : JsonDeserializer<Tag> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Tag {
        val obj = json?.asJsonObject!!

        return Tag(
                name = obj.get("name").asString,
                fontColor = parseColor(obj.get("fontColor").asString),
                color = parseColor(obj.get("color").asString)
        )
    }
}

