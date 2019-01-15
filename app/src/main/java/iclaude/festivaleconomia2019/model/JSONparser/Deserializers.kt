package iclaude.festivaleconomia2019.model.JSONparser

import android.graphics.Color.parseColor
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import iclaude.festivaleconomia2019.model.data_classes.Location
import iclaude.festivaleconomia2019.model.data_classes.Organizer
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

