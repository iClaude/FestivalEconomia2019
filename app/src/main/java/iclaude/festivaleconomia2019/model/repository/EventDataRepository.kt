package iclaude.festivaleconomia2019.model.repository

import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.JSONparser.JSONparser
import java.io.InputStream


class EventDataRepository(private val inputStream: InputStream) {

    private val TAG = "EventDataRepository"

    fun loadEventData(): EventData {
        return JSONparser.parseEventData(inputStream)
    }

}