package iclaude.festivaleconomia2019.model.JSONparser

import com.google.gson.GsonBuilder
import java.io.InputStream

object JSONparser {
    fun parseEventData(inputStream: InputStream): EventData {
        val jsonReader = com.google.gson.stream.JsonReader(inputStream.reader())
        val gson = GsonBuilder().create()
        return gson.fromJson(jsonReader, EventData::class.java)
    }
}