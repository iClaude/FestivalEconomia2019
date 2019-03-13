package iclaude.festivaleconomia2019.ui.details.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.data_classes.Session
import iclaude.festivaleconomia2019.model.data_classes.Tag
import iclaude.festivaleconomia2019.model.data_classes.hasPhotoUrl
import iclaude.festivaleconomia2019.model.data_classes.hasYoutubeUrl
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


    // Load session info when repository is ready.

    private val _sessionInfoLoadedEvent = MutableLiveData<Event<SessionInfo>>()
    val sessionInfoLoadedEvent: LiveData<Event<SessionInfo>>
        get() = _sessionInfoLoadedEvent

    fun loadSessionInfo() {
        val eventData: EventData = eventDataFromRepoLive.value ?: return

        val id = sessionId.toInt()
        val session = eventData.sessions[id]

        sessionInfo = SessionInfo(
            session.id,
            session.title,
            session.startTimestamp,
            session.endTimestamp,
            getTags(session, eventData.tags),
            if (session.hasPhotoUrl()) session.photoUrl else null,
            if (session.hasYoutubeUrl()) session.youtubeUrl else null
        )

        // Session info loaded: communicate it to Fragment in order to bind data to layout.
        _sessionInfoLoadedEvent.value = Event(sessionInfo)
    }

    private fun getTags(session: Session, tagsList: List<Tag>): List<Tag> {
        val tags = mutableListOf<Tag>()
        for (tagId in session.tags) {
            tags.add(tagsList[tagId.toInt()])
        }
        return tags
    }

    // User clicks the button in the app bar to watch the YouTube video of the event.

    private val _startYoutubeVideoEvent = MutableLiveData<Event<String>>()
    val startYoutubeVideoEvent: LiveData<Event<String>>
        get() = _startYoutubeVideoEvent

    fun startYoutubeVideo() {
        val url = sessionInfo.youtubeUrl ?: return

        _startYoutubeVideoEvent.value = Event(url)
    }

    // User clicks the arrow in the app bar to navigate to previous Fragment.

    private val _navigateBackEvent = MutableLiveData<Event<Any>>()
    val navigateBackEvent: LiveData<Event<Any>>
        get() = _navigateBackEvent

    fun navigateBack() {
        _navigateBackEvent.value = Event(Unit)
    }
}

// Info to display in the layout.
class SessionInfo(
    val id: String,
    val title: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val tags: List<Tag>,
    val photoUrl: String?,
    val youtubeUrl: String?
)