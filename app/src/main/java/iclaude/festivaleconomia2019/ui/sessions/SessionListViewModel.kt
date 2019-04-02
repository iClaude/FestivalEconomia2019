package iclaude.festivaleconomia2019.ui.sessions

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.databinding.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.data_classes.Tag
import iclaude.festivaleconomia2019.model.data_classes.User
import iclaude.festivaleconomia2019.model.data_classes.hasYoutubeUrl
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import iclaude.festivaleconomia2019.ui.login.LoginFlow
import iclaude.festivaleconomia2019.ui.sessions.filters.*
import iclaude.festivaleconomia2019.ui.utils.Event
import iclaude.festivaleconomia2019.ui.utils.sessionLength
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SessionListViewModel(val context: Application) : AndroidViewModel(context), LoginFlow {

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

    /**
     * Event data fetched from repository.
     */

    @Inject
    lateinit var repository: EventDataRepository
    val eventDataFromRepoLive = Transformations.switchMap(repository.eventDataLive) {
        MutableLiveData<EventData>().apply {
            value = it
        }
    }

    /**
     * Initializations.
     */

    private fun loadDataFromRepo() {
        if (!repository.dataLoaded) repository.loadEventDataFromJSONFile()
    }

    private fun loadUserInfo() {
        FirebaseAuth.getInstance().currentUser?.let {
            showUserPhoto(it)
        }
    }

    /**
     * Filters.
     */

    var filter: Filter = Filter() // filter currently applied

    // Does current filter have tags?
    private val _isFilterTagged = MutableLiveData<Boolean>().apply { value = false }
    val isFilterTagged: LiveData<Boolean>
        get() = _isFilterTagged

    // Does current filter include starred sessions?
    private val _isFilterStarred = MutableLiveData<Boolean>().apply { value = false }
    val isFilterStarred: LiveData<Boolean>
        get() = _isFilterStarred

    // List of selected tags (filters) to show when filter sheet is collapsed.
    private val _filterTags = MutableLiveData<List<Tag>>().apply { value = mutableListOf() }
    val filterTags: LiveData<List<Tag>>
        get() = _filterTags


    /**
     * Sessions list.
     */

    private var sessions: MutableList<SessionInfoForList> = mutableListOf()

    private val _isDataLoaded = MutableLiveData<Boolean>().apply { value = false }
    val isDataLoaded: LiveData<Boolean>
        get() = _isDataLoaded

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
                    session.hasYoutubeUrl(),
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
                _isDataLoaded.value = true
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
                _numOfSessionsFiltered.value = filteredList.size
                _isFilterTagged.value = filter.hasTags()
                _isFilterStarred.value = filter.isStarred()
                _filterTags.value = mutableListOf<Tag>().apply {
                    if (filter.isStarred()) this.add(starredTag)
                    this.addAll(filter.tagsTypes + filter.tagsTopics)
                }
            }
        }
    }

    /**
     * Filtering BottomSheet.
     */

    // Operations.

    // Reset button is clicked when filter sheet is expanded: all filters are cleared.
    fun clearFilters() {
        filter.clear()
        _clearTags.value = clearTags.value!!.plus(1)
        filterList()
    }

    // Clear filters button is clicked when filter sheet is collapsed: clear filter and hide bottom sheet.
    fun clearFiltersAndCollapse() {
        clearFilters()
        _removeFilterSheetEvent.value = Event(Unit)
    }


    // BottomSheet UI.
    lateinit var starredTag: Tag

    // Number of filtered sessions (to display in filter sheets when some tags are selected).
    private val _numOfSessionsFiltered = MutableLiveData<Int>().apply { value = 0 }
    val numOfSessionsFiltered: LiveData<Int>
        get() = _numOfSessionsFiltered


    val tagsObs: ObservableList<Tag> = ObservableArrayList() // list of all tags: used to add Chips to ChipGroups
    private fun loadAllTags() {
        val tags = repository.eventDataLive.value?.tags
        tagsObs.clear()
        tagsObs.addAll(tags ?: return)
    }

    // When filter is cleared all Chips in ChipGroups must be unchecked.
    private val _clearTags = MutableLiveData<Int>().apply { value = 0 }
    val clearTags: LiveData<Int>
        get() = _clearTags

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
        _changeFilterSheetStateEvent.value = when (isFilterTagged.value!! || isFilterStarred.value!!) {
            true -> Event(STATE_COLLAPSED)
            else -> Event(STATE_HIDDEN)
        }
    }

    /**
     * User authentication.
     */

    override val _authEvent: MutableLiveData<Event<LoginFlow.Authentication>> = MutableLiveData()

    override fun onUserLoggedIn(user: FirebaseUser) {
        showUserPhoto(user)
        addUserToFirebase(user)
        updateSessionListWithStarredSessions()
    }

    override fun onUserLoggedOut() {
        userImageUriObs.set(null)
        sessions.forEach {
            it.starred = false
        }
        filterList()
    }

    override fun addUserToFirebase(user: FirebaseUser) {
        repository.addUser(user)
    }

    fun showUserPhoto(user: FirebaseUser) {
        for (profile in user.providerData) {
            val photoUri = profile.photoUrl
            photoUri?.let {
                val oldPhotoUri = userImageUriObs.get()
                if (it != oldPhotoUri) userImageUriObs.set(it)
                return
            }
        }
    }

    /**
     * Starred sessions for logged-in users.
     */

    private val _loginDataUpdateEvent = MutableLiveData<Event<Any>>()
    val loginDataUpdateEvent: LiveData<Event<Any>>
        get() = _loginDataUpdateEvent

    // When data is fetched from Firebase, hide the progress bar of SwipeRefreshLayout, if present.
    enum class FirebaseResult { SUCCESS, ERROR }

    private val _dataFetchedFromFirebaseEvent = MutableLiveData<Event<FirebaseResult>>()
    val dataFetchedFromFirebaseEvent: LiveData<Event<FirebaseResult>>
        get() = _dataFetchedFromFirebaseEvent

    fun loginDataNeedsUpdate() {
        _loginDataUpdateEvent.value = Event(Unit)
    }

    fun updateSessionListWithStarredSessions() {
        repository.getStarredSessions(
            OnSuccessListener { documentSnapshot ->
                val userInFirebase = documentSnapshot.toObject(User::class.java)
                userInFirebase?.let { userInFirebase ->
                    if (userInFirebase.starredSessions.isEmpty()) return@OnSuccessListener

                    sessions.forEach { it.starred = false }
                    userInFirebase.starredSessions.forEach {
                        sessions[it.toInt()].starred = true
                    }
                    filterList()
                }
                _dataFetchedFromFirebaseEvent.value = Event(FirebaseResult.SUCCESS)
            },
            OnFailureListener {
                Log.w(iclaude.festivaleconomia2019.utils.TAG, "Error getting user in Firebase", it)
                _dataFetchedFromFirebaseEvent.value = Event(FirebaseResult.ERROR)
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


    /**
     * Session's detailed info.
     */

    private val _goToSessionEvent = MutableLiveData<Event<String>>()
    val goToSessionEvent: LiveData<Event<String>>
        get() = _goToSessionEvent

    fun goToSession(sessionId: String) {
        _goToSessionEvent.value = Event(sessionId)
    }

    // ViewModel destroyed.
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