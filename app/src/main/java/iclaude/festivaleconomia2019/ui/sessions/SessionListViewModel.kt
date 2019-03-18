package iclaude.festivaleconomia2019.ui.sessions

import android.app.Application
import android.net.Uri
import androidx.databinding.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.data_classes.Tag
import iclaude.festivaleconomia2019.model.data_classes.User
import iclaude.festivaleconomia2019.model.data_classes.hasSessionUrl
import iclaude.festivaleconomia2019.model.data_classes.hasYoutubeUrl
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import iclaude.festivaleconomia2019.ui.sessions.filters.*
import iclaude.festivaleconomia2019.ui.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SessionListViewModel(val context: Application) : AndroidViewModel(context) {

    private val viewModelJob = Job()
    private val defaultScope = CoroutineScope(Dispatchers.Default + viewModelJob)
    private val mainScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // User info.
    val userImageUriObs: ObservableField<Uri> = ObservableField()

    init {
        App.component.inject(this)
        loadDataFromRepo()
        loadUserInfo()
    }

    // Event data fetched from repository.

    @Inject
    lateinit var repository: EventDataRepository
    val eventDataFromRepoLive = Transformations.switchMap(repository.eventDataLive) {
        MutableLiveData<EventData>().apply {
            value = it
        }
    }

    // Initializations.

    private fun loadDataFromRepo() {
        if (!repository.dataLoaded) repository.loadEventDataFromJSONFile()
    }

    private fun loadUserInfo() {
        FirebaseAuth.getInstance().currentUser?.let {
            showUserPhoto(it)
        }
    }

    // Filters.

    var filter: Filter = Filter() // filter currently applied
    val isFilterTaggedObs: ObservableBoolean = ObservableBoolean(false) // does current filter have tags?
    val isFilterStarredObs: ObservableBoolean =
        ObservableBoolean(false) // does current filter include starred sessions?
    val filterTagsObs: ObservableList<Tag> =
        ObservableArrayList() // list of selected tags (filters) to show when filter sheet is collapsed


    // Sessions list.

    private var sessions: MutableList<SessionInfoForList> = mutableListOf()

    val dataLoadedObs: ObservableBoolean = ObservableBoolean(false)

    // List of filtered sessions.
    private val _sessionsInfoFilteredLive: MutableLiveData<List<SessionInfoForList>> = MutableLiveData()
    val sessionsInfoFilteredLive: LiveData<List<SessionInfoForList>>
        get() = _sessionsInfoFilteredLive


    // Load original list of sessions when data is loaded from repository: triggered from SessionContainerFragment.
    fun loadInfoList(eventData: EventData) {
        if (sessions.isNotEmpty()) return

        defaultScope.launch {
            sessions = eventData.sessions.map { session ->
                SessionInfoForList(
                    session.id, session.title,
                    session.hasSessionUrl() || (session.hasYoutubeUrl()),
                    session.startTimestamp,
                    session.endTimestamp,
                    "${sessionLength(
                        context,
                        session.startTimestamp,
                        session.endTimestamp
                    )} / ${eventData.locations[session.location.toInt()].name}",
                    session.tags.map {
                        eventData.tags[it.toInt()]
                    },
                    session.day,
                    false
                )
            }.toMutableList()

            mainScope.launch {
                dataLoadedObs.set(true)
                _sessionsInfoFilteredLive.value = sessions
                loadAllTags()
                updateSessionListWithStarredSessions()
            }
        }
    }

    fun filterList() {
        defaultScope.launch {
            var filteredList = sessions.toMutableList()

            // filter by tags and starred
            if (filter.isFilterSet()) filteredList = sessions.filter {
                if (filter.isStarred()) it.starred else true
            }.filter {
                if (filter.hasTypeTags())
                    it.tags.intersect(filter.tagsTypes).isNotEmpty()
                else
                    true
            }.filter {
                if (filter.hasTopicTags())
                    it.tags.intersect(filter.tagsTopics).isNotEmpty()
                else
                    true
            }.toMutableList()

            mainScope.launch {
                _sessionsInfoFilteredLive.value = filteredList
                sessionsFilteredObs.set(filteredList.size)
                isFilterTaggedObs.set(filter.hasTags())
                isFilterStarredObs.set(filter.isStarred())
                filterTagsObs.apply {
                    clear()
                    if (filter.isStarred()) this.add(starredTag)
                    this.addAll(filter.tagsTypes + filter.tagsTopics)
                }
            }
        }
    }

    // Filtering BottomSheet.

    // Operations.

    // Reset button is clicked when filter sheet is expanded: all filters are cleared.
    fun clearFilters() {
        filter.clear()
        clearTagsObs.set(clearTagsObs.get() + 1)
        filterList()
    }

    // Clear filters button is clicked when filter sheet is collapsed: clear filter and hide bottom sheet.
    fun clearFiltersAndCollapse() {
        clearFilters()
        _removeFilterSheetEvent.value = Event(Unit)
    }


    // BottomSheet UI.
    lateinit var starredTag: Tag

    val sessionsFilteredObs: ObservableInt =
        ObservableInt(0) // number of filtered sessions (to display in filter sheets when some tags are selected)

    val tagsObs: ObservableList<Tag> = ObservableArrayList() // list of all tags: used to add Chips to ChipGroups
    private fun loadAllTags() {
        val tags = repository.eventDataLive.value?.tags
        tagsObs.clear()
        tagsObs.addAll(tags ?: return)
    }

    val clearTagsObs: ObservableInt =
        ObservableInt(0) // when filter is cleared all Chips in ChipGroups must be unchecked

    val titleHeaderAlphaObs: ObservableFloat = ObservableFloat(0f) // change header alpha when dragging bottom sheet

    val scrollYObs: ObservableInt =
        ObservableInt(0) // scroll view inside bottom sheet y offset (used to change header elevation)

    // BottomSheet expand/collapse states.

    private val _changeFilterSheetStateEvent = MutableLiveData<Event<Int>>()
    val changeFilterSheetStateEvent: LiveData<Event<Int>>
        get() = _changeFilterSheetStateEvent

    private val _removeFilterSheetEvent = MutableLiveData<Event<Any>>()
    val removeFilterSheetEvent: LiveData<Event<Any>>
        get() = _removeFilterSheetEvent

    fun changeFilterSheetState(toExpand: Boolean) {
        // expand
        if (toExpand) {
            _changeFilterSheetStateEvent.value = Event(STATE_EXPANDED)
            return
        }

        // collapse or hide depending on filters
        _changeFilterSheetStateEvent.value = when (isFilterTaggedObs.get() || isFilterStarredObs.get()) {
            true -> Event(STATE_COLLAPSED)
            else -> Event(STATE_HIDDEN)
        }
    }

    // User authentication.

    enum class Authentication { LOGIN_REQUEST, LOGIN_CONFIRMED, LOGOUT_REQUEST, LOGOUT_CONFIRMED }

    private val _authEvent = MutableLiveData<Event<Authentication>>()
    val authEvent: LiveData<Event<Authentication>>
        get() = _authEvent

    fun startAuthFlow() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            _authEvent.value = Event(Authentication.LOGIN_REQUEST)
        } else {
            _authEvent.value = Event(Authentication.LOGOUT_REQUEST)
        }
    }

    fun confirmLogin() {
        _authEvent.value = Event(Authentication.LOGIN_CONFIRMED)
    }

    fun confirmLogout() {
        _authEvent.value = Event(Authentication.LOGOUT_CONFIRMED)
    }

    fun getLoginProviders() = arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.FacebookBuilder().build(),
        AuthUI.IdpConfig.TwitterBuilder().build()
    )

    fun onUserLoggedIn(user: FirebaseUser) {
        showUserPhoto(user)
        addUserToFirebase(user)
        updateSessionListWithStarredSessions()
    }

    fun onUserLoggedOut() {
        userImageUriObs.set(null)
        sessions.forEach {
            it.starred = false
        }
        filterList()
    }

    fun addUserToFirebase(user: FirebaseUser) {
        repository.addUser(user)
    }

    fun showUserPhoto(user: FirebaseUser) {
        for (profile in user.providerData) {
            val photoUri = profile.photoUrl
            photoUri?.let {
                userImageUriObs.set(it)
                return
            }
        }
    }

    // Starred sessions for logged-in users.

    private val _starredSessionsUpdateEvent = MutableLiveData<Event<Any>>()
    val starredSessionsUpdateEvent: LiveData<Event<Any>>
        get() = _starredSessionsUpdateEvent

    fun starredSessionsNeedUpdate() {
        _starredSessionsUpdateEvent.value = Event(Unit)
    }

    fun updateSessionListWithStarredSessions() {
        repository.getStarredSessions(OnSuccessListener { documentSnapshot ->
            val userInFirebase = documentSnapshot.toObject(User::class.java)
            userInFirebase?.let { userInFirebase ->
                if (userInFirebase.starredSessions.isEmpty()) return@OnSuccessListener

                sessions.forEach { it.starred = false }
                userInFirebase.starredSessions.forEach {
                    sessions[it.toInt()].starred = true
                }
                filterList()
            }
        })
    }

    private val _showSnackBarForStarringEvent = MutableLiveData<Event<Boolean>>()
    val showSnackBarForStarringEvent: LiveData<Event<Boolean>>
        get() = _showSnackBarForStarringEvent

    fun starOrUnstarSession(sessionId: String, toStar: Boolean) {
        repository.starOrUnstarSession(sessionId, toStar)
        sessions[sessionId.toInt()].starred = toStar
        _showSnackBarForStarringEvent.value = Event(toStar)
    }


    // Session's detailed info.
    private val _goToSessionEvent = MutableLiveData<Event<String>>()
    val goToSessionEvent: LiveData<Event<String>>
        get() = _goToSessionEvent

    fun goToSession(sessionId: String) {
        _goToSessionEvent.value = Event(sessionId)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        repository.cancelLoadingData()
    }

}

// Sessions' info to display in the list.
class SessionInfoForList(
    val id: String,
    val title: String,
    val liveStreamed: Boolean,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val lenLoc: String,
    val tags: List<Tag>,
    var day: Int,
    var starred: Boolean
)