package iclaude.festivaleconomia2019.ui.details.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.data_classes.*
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
            session.hasSessionUrl() || session.hasYoutubeUrl(),
            eventData.locations[session.location.toInt()].displayString
                ?: eventData.locations[session.location.toInt()].name,
            session.description,
            getTags(session, eventData.tags),
            getOrganizers(session, eventData.organizers),
            getRelatedSessions(session, eventData.sessions),
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

    private fun getOrganizers(session: Session, organizersList: List<Organizer>): List<Organizer> {
        val organizers = mutableListOf<Organizer>()
        for (organizerId in session.organizers) {
            organizers.add(organizersList[organizerId.toInt()])
        }
        return organizers
    }

    private fun getRelatedSessions(session: Session, sessionList: List<Session>): List<Session> {
        val sessions = mutableListOf<Session>()
        session.relatedSessions ?: return sessions

        for (sessionId in session.relatedSessions) {
            sessions.add(sessionList[sessionId.toInt()])
        }
        return sessions
    }

    // User clicks the button in the app bar to watch the YouTube video of the event.

    private val _startYoutubeVideoEvent = MutableLiveData<Event<String>>()
    val startYoutubeVideoEvent: LiveData<Event<String>>
        get() = _startYoutubeVideoEvent

    fun startYoutubeVideo() {
        val url = sessionInfo.youtubeUrl ?: return

        _startYoutubeVideoEvent.value = Event(url)
    }

    // User clicks to a related session to navigate to it.

    private val _goToSessionEvent = MutableLiveData<Event<String>>()
    val goToSessionEvent: LiveData<Event<String>>
        get() = _goToSessionEvent

    fun goToSession(sessionId: String) {
        _goToSessionEvent.value = Event(sessionId)
    }
}

// Info to display in the layout.
class SessionInfo(
    val id: String,
    val title: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val liveStreamed: Boolean,
    val location: String,
    val description: String,
    val tags: List<Tag>,
    val organizers: List<Organizer>,
    val relatedSessions: List<Session>?,
    val photoUrl: String?,
    val youtubeUrl: String?
)

fun SessionInfo.hasRelatedSessions() = relatedSessions?.isNotEmpty() ?: false