package iclaude.festivaleconomia2019.ui.details.organizer

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

class OrganizerViewModel : ViewModel(), LoginFlow, RelatedSessions {
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

    lateinit var organizerId: String
    lateinit var organizerInfo: OrganizerInfo

    /**
     * Load organizer info when repository is ready.
     */

    private val _organizerInfoLoadedEvent = MutableLiveData<Event<OrganizerInfo>>()
    val organizerInfoLoadedEvent: LiveData<Event<OrganizerInfo>>
        get() = _organizerInfoLoadedEvent

    fun loadOrganizerInfo() {
        val eventData: EventData = eventDataFromRepoLive.value ?: return

        val organizer = eventData.organizers[organizerId.toInt()]

        organizerInfo = OrganizerInfo(
            organizer.id,
            organizer.name,
            organizer.bio,
            eventData.sessions.filter {
                it.organizers.contains(organizerId)
            },
            if (organizer.hasCompany()) organizer.company else null,
            if (organizer.hasThumbnailUrl()) organizer.thumbnailUrl else null,
            if (organizer.hasWebsiteUrl()) organizer.websiteUrl else null,
            if (organizer.hastwitterUrl()) organizer.twitterUrl else null,
            if (organizer.haslinkedInUrl()) organizer.linkedInUrl else null,
            if (organizer.hasfacebookUrl()) organizer.facebookUrl else null
        )

        // Session info loaded: communicate it to Fragment in order to bind data to layout.
        _organizerInfoLoadedEvent.value = Event(organizerInfo)
    }

    /**
     * AppBar info.
     */

    // When app bar is collapsed we need to display organizer's name in the Toolbar.
    private val _appBarCollapsedPercentage = MutableLiveData<Float>().apply { value = 0f }
    val appBarCollapsedPercentage: LiveData<Float>
        get() = _appBarCollapsedPercentage

    fun setAppBarCollapsedPercentage(perc: Float) {
        _appBarCollapsedPercentage.value = perc
    }

    // Avatar has been loaded.
    private val _avatarLoadedEvent = MutableLiveData<Event<Any>>()
    val avatarLoadedEvent: LiveData<Event<Any>>
        get() = _avatarLoadedEvent

    fun avatarWasLoaded() {
        _avatarLoadedEvent.value = Event(Unit)
    }

    /**
     * User clicks on a related session to navigate to it.
     */
    override val _goToSessionEvent = MutableLiveData<Event<String>>()

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
                }
            },
            OnFailureListener { Log.w(TAG, "Error getting user in Firebase", it) })
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
}

// Info to display in the layout.
class OrganizerInfo(
// required data
    val id: String,
    val name: String,
    val bio: String,
    val sessions: List<Session>?,
    // optional data
    val company: String?,
    val thumbnailUrl: String?,
    val websiteUrl: String?,
    val twitterUrl: String?,
    val linkedInUrl: String?,
    val facebookUrl: String?
)

fun OrganizerInfo.hasSessions() = sessions?.isNotEmpty() ?: false