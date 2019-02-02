package iclaude.festivaleconomia2019.ui.sessions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.data_classes.hasSessionUrl
import iclaude.festivaleconomia2019.model.data_classes.hasYoutubeUrl
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import javax.inject.Inject

class SessionsViewModel : ViewModel() {
    private val TAG = "MY_BINDING_ADAPTER"

    init {
        App.component.inject(this)
    }

    @Inject
    lateinit var mRepository: EventDataRepository

    private var _sessionsInfoLive: MutableLiveData<List<SessionsDisplayInfo>> = MutableLiveData()
    val sessionsInfoLive: LiveData<List<SessionsDisplayInfo>>
        get() = _sessionsInfoLive

    private val _dataLoadedLive: MutableLiveData<Boolean> = MutableLiveData()
    val dataLoadedLive: LiveData<Boolean>
        get() = _dataLoadedLive

    fun loadData(eventData: EventData) {
        val sessions = eventData.sessions.map {
            SessionsDisplayInfo(
                it.id, it.title,
                it.hasSessionUrl() || (it.hasYoutubeUrl()),
                it.startTimestamp,
                it.endTimestamp,
                eventData.locations[it.location.toInt()].name
            )
        }
        _sessionsInfoLive.value = sessions

        _dataLoadedLive.value = true
    }
}

class SessionsDisplayInfo(
    val id: String,
    val title: String,
    val liveStreamed: Boolean,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val location: String
)