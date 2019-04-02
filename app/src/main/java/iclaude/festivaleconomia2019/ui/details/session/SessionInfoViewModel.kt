package iclaude.festivaleconomia2019.ui.details.session

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseUser
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.data_classes.*
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import iclaude.festivaleconomia2019.ui.details.RelatedSessions
import iclaude.festivaleconomia2019.ui.login.LoginFlow
import iclaude.festivaleconomia2019.ui.utils.Event
import iclaude.festivaleconomia2019.utils.TAG
import javax.inject.Inject

class SessionInfoViewModel : ViewModel(), LoginFlow, RelatedSessions {
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

    var locationId: Int = -1

    lateinit var sessionId: String
    lateinit var sessionInfo: SessionInfo


    /**
     * Load session info when repository is ready.
     */

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
            session.hasYoutubeUrl(),
            eventData.locations[session.location.toInt()].displayString
                ?: eventData.locations[session.location.toInt()].name,
            session.description,
            getTags(session, eventData.tags),
            getOrganizers(session, eventData.organizers),
            getRelatedSessions(session, eventData.sessions),
            if (session.hasPhotoUrl()) session.photoUrl else null,
            if (session.hasYoutubeUrl()) session.youtubeUrl else null,
            if (session.hasSessionUrl()) session.sessionUrl else null
        )

        locationId = session.location.toInt()

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

    /**
     * User clicks the button in the app bar to watch the YouTube video of the event.
     */

    private val _startYoutubeVideoEvent = MutableLiveData<Event<String>>()
    val startYoutubeVideoEvent: LiveData<Event<String>>
        get() = _startYoutubeVideoEvent

    fun startYoutubeVideo() {
        val url = sessionInfo.youtubeUrl ?: return

        _startYoutubeVideoEvent.value = Event(url)
    }

    /**
     * User clicks on a related session to navigate to it.
     */
    override val _goToSessionEvent = MutableLiveData<Event<String>>()


    /**
     * User clicks on an organizer to navigate to it.
     */

    private val _goToOrganizerEvent = MutableLiveData<Event<String>>()
    val goToOrganizerEvent: LiveData<Event<String>>
        get() = _goToOrganizerEvent

    fun goToOrganizer(organizerId: String) {
        _goToOrganizerEvent.value = Event(organizerId)
    }


    /**
     * Starred sessions (related events).
     */

    // If this session is starred the FAB should be checked.
    private val _isThisSessionStarred = MutableLiveData<Boolean>().apply { value = false }
    val isThisSessionStarred: LiveData<Boolean>
        get() = _isThisSessionStarred

    /**
     * Find starred sessions for logged-in users.
     */

    private val _starredSessionsLive = MutableLiveData<List<String>>()
    val starredSessionsLive: LiveData<List<String>>
        get() = _starredSessionsLive

    fun findStarredSessions() {
        repository.getStarredSessions(
            OnSuccessListener { documentSnapshot ->
                val userInFirebase = documentSnapshot.toObject(User::class.java)
                userInFirebase?.let { userInFirebase ->
                    if (userInFirebase.starredSessions.isEmpty()) return@OnSuccessListener

                    _starredSessionsLive.value = userInFirebase.starredSessions
                    _isThisSessionStarred.value = userInFirebase.starredSessions.contains(sessionInfo.id)
                }
            },
            OnFailureListener { Log.w(TAG, "Error getting user in Firebase", it) })
    }

    /**
     * User stars/unstars a related session.
     * Implementation of RelatedSessions interface.
     */

    /* If the user logs in-out or stars/unstars sessions in this Fragment, SessionListFragment
       should also be updated (showing avatar and updating starred sessions). */
    override val _loginOperationsEvent = MutableLiveData<Event<Any>>()

    override val _showSnackBarForStarringEvent = MutableLiveData<Event<Boolean>>()

    override fun starOrUnstarSession(sessionId: String, toStar: Boolean) {
        repository.starOrUnstarSession(sessionId, toStar)
        super.starOrUnstarSession(sessionId, toStar)
    }

    /**
     * User authentication.
     * Implementation of LoginFlow interface.
     */

    override val _authEvent: MutableLiveData<Event<LoginFlow.Authentication>> = MutableLiveData()

    override fun onUserLoggedIn(user: FirebaseUser) {
        addUserToFirebase(user)
        findStarredSessions()
        _loginOperationsEvent.value = Event(Unit)
    }

    override fun onUserLoggedOut() {
        // no need to log out from this Fragment
    }

    override fun addUserToFirebase(user: FirebaseUser) {
        repository.addUser(user)
    }

    /**
     * AppBar info.
     */

    // When app bar is collapsed we need to display the title (or generic info) in the Toolbar.
    private val _appBarCollapsedPercentage = MutableLiveData<Float>().apply { value = 0f }
    val appBarCollapsedPercentage: LiveData<Float>
        get() = _appBarCollapsedPercentage

    fun setAppBarCollapsedPercentage(perc: Float) {
        _appBarCollapsedPercentage.value = perc
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
    val youtubeUrl: String?,
    val sessionUrl: String?
)

fun SessionInfo.hasRelatedSessions() = relatedSessions?.isNotEmpty() ?: false