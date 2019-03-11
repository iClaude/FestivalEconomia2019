package iclaude.festivaleconomia2019.ui.sessioninfo

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import iclaude.festivaleconomia2019.ui.utils.Event
import javax.inject.Inject

class SessionInfoViewModel : ViewModel() {
    @Inject
    lateinit var repository: EventDataRepository

    init {
        App.component.inject(this)
    }

    val eventDataFromRepoLive = Transformations.switchMap(repository.eventDataLive) {
        MutableLiveData<EventData>().apply {
            value = it
        }
    }

    lateinit var sessionId: String
    private lateinit var sessionInfo: SessionInfo

    val imageUrlObs = ObservableField<String>()
    val youtubeUrlObs = ObservableField<String>()

    // Load session info when repository is ready.
    fun loadSessionInfo() {
        val eventData: EventData = eventDataFromRepoLive.value ?: return

        val id = sessionId.toInt()
        sessionInfo = SessionInfo(
            eventData.sessions[id].id,
            eventData.sessions[id].photoUrl,
            eventData.sessions[id].youtubeUrl
        )

        // Load observables for data binding.
        imageUrlObs.set(sessionInfo.photoUrl)
        youtubeUrlObs.set(sessionInfo.youtubeUrl)
    }

    private val _startYoutubeVideoEvent = MutableLiveData<Event<String>>()
    val startYoutubeVideoEvent: LiveData<Event<String>>
        get() = _startYoutubeVideoEvent

    fun startYoutubeVideo() {
        val url = youtubeUrlObs.get() ?: return

        _startYoutubeVideoEvent.value = Event(url)
    }
}

// Info to display in the layout.
class SessionInfo(
    val id: String,
    val photoUrl: String?,
    val youtubeUrl: String?
)