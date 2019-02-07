package iclaude.festivaleconomia2019.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.JSONparser.JSONparser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.InputStream


class EventDataRepository(private val inputStream: InputStream) {
    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    private val _eventDataLive: MutableLiveData<EventData> = MutableLiveData()
    val eventDataLive: LiveData<EventData>
        get() = _eventDataLive

    fun loadEventData() {
        ioScope.launch {
            _eventDataLive.postValue(JSONparser.parseEventData(inputStream))
        }
    }

    fun canceLoadingData() {
        job.cancel()
    }
}