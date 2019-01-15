package iclaude.festivaleconomia2019.model.JSONparser

import com.google.gson.GsonBuilder
import iclaude.festivaleconomia2019.model.data_classes.Location
import iclaude.festivaleconomia2019.model.data_classes.Organizer
import iclaude.festivaleconomia2019.model.data_classes.Session
import iclaude.festivaleconomia2019.model.data_classes.Tag
import java.io.InputStream

object JSONparser {
    fun parseEventData(inputStream: InputStream): EventData {
        val jsonReader = com.google.gson.stream.JsonReader(inputStream.reader())
        val gson = GsonBuilder()
                .registerTypeAdapter(Location::class.java, LocationDeserializer())
                .registerTypeAdapter(Session::class.java, SessionDeserializer())
                .registerTypeAdapter(Organizer::class.java, OrganizerDeserializer())
                .registerTypeAdapter(Tag::class.java, TagDeserializer())
                .create()
        return gson.fromJson(jsonReader, EventData::class.java)
    }
}