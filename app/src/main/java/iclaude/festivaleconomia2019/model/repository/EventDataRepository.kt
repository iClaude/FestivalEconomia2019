package iclaude.festivaleconomia2019.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.JSONparser.JSONparser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream


class EventDataRepository(private val inputStream: InputStream) {

    private var mEventDataMutableLive: MutableLiveData<EventData> = MutableLiveData()
    val eventDataLive: LiveData<EventData>
        get() = mEventDataMutableLive


    init {
        GlobalScope.launch {
            mEventDataMutableLive.postValue(JSONparser.parseEventData(inputStream))
        }
    }


}