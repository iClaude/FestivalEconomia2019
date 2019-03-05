package iclaude.festivaleconomia2019.ui.sessions

import android.app.Application
import android.net.Uri
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.databinding.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.data_classes.Tag
import iclaude.festivaleconomia2019.model.data_classes.User
import iclaude.festivaleconomia2019.model.data_classes.hasSessionUrl
import iclaude.festivaleconomia2019.model.data_classes.hasYoutubeUrl
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import iclaude.festivaleconomia2019.ui.sessions.filters.*
import iclaude.festivaleconomia2019.ui.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SessionListViewModel(val context: Application) : AndroidViewModel(context) {

    private val viewModelJob = Job()
    private val defaultScope = CoroutineScope(Dispatchers.Default + viewModelJob)
    private val mainScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    // ************************ Filters ******************************
    var filter: Filter = Filter() // filter currently applied
    val isFilterTaggedObs: ObservableBoolean = ObservableBoolean(false) // does current filter have tags?
    val isFilterStarredObs: ObservableBoolean =
        ObservableBoolean(false) // does current filter include starred sessions?
    val filterTagsObs: ObservableList<Tag> =
        ObservableArrayList() // list of selected tags (filters) to show when filter sheet is collapsed


    // ********************* Sessions list ******************************
    @Inject
    lateinit var repository: EventDataRepository

    private var sessions: MutableList<SessionInfoForList> = mutableListOf()

    val dataLoadedObs: ObservableBoolean = ObservableBoolean(false)

    // List of filtered sessions.
    private val _sessionsInfoFilteredLive: MutableLiveData<List<SessionInfoForList>> = MutableLiveData()
    val sessionsInfoFilteredLive: LiveData<List<SessionInfoForList>>
        get() = _sessionsInfoFilteredLive


    // load original list of sessions when data is loaded from repository: triggered from SessionContainerFragment
    fun loadInfoList(eventData: EventData) {
        if (sessions.isNotEmpty()) return

        defaultScope.launch {
            sessions = eventData.sessions.map { session ->
                SessionInfoForList(
                    session.id, session.title,
                    session.hasSessionUrl() || (session.hasYoutubeUrl()),
                    session.startTimestamp,
                    session.endTimestamp,
                    eventData.locations[session.location.toInt()].name,
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

    // ********************** filtering BottomSheet ******************************
    // operations
    fun filterUpdated() {
        filterList()
    }

    // reset button is clicked when filter sheet is expanded: all filters are cleared
    fun clearFilters() {
        filter.clear()
        clearTagsObs.set(clearTagsObs.get() + 1)
        filterList()
    }

    // clear filters button is clicked when filter sheet is collapsed: clear filter and hide bottom sheet
    fun clearFiltersAndCollapse() {
        clearFilters()
        removeFilterSheetCommand.call()
    }

    // chip for starred sessions is checked/unchecked
    fun chipStarredCheckedChanged(compoundButton: CompoundButton, isChecked: Boolean) {
        filter.starred = isChecked
        filterList()
    }

    // BottomSheet UI
    private val starredTag = Tag( // tag for favorite sessions
        "99",
        "none",
        context.getString(R.string.filter_favorites),
        Integer.toHexString(ContextCompat.getColor(context, R.color.onSurfaceColor)),
        "#" + Integer.toHexString(ContextCompat.getColor(context, R.color.secondaryColor))
    )

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

    // BottomSheet expand/collapse states
    val changeFilterSheetStateCommand: SingleLiveEvent<Int> = SingleLiveEvent()
    val removeFilterSheetCommand: SingleLiveEvent<Void> = SingleLiveEvent()

    fun changeFilterSheetState(toExpand: Boolean) {
        // expand
        if (toExpand) {
            changeFilterSheetStateCommand.value = STATE_EXPANDED
            return
        }

        // collapse or hide depending on filters
        changeFilterSheetStateCommand.value = when (isFilterTaggedObs.get() || isFilterStarredObs.get()) {
            true -> BottomSheetBehavior.STATE_COLLAPSED
            else -> BottomSheetBehavior.STATE_HIDDEN
        }
    }

    //***************************** User authentication *************************************
    val userImageUriObs: ObservableField<Uri> = ObservableField()

    enum class Authentication { LOGIN, LOGOUT, LOGIN_FROM_STAR }

    val authCommand: SingleLiveEvent<Authentication> = SingleLiveEvent()

    fun onProfileClicked() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            authCommand.value = Authentication.LOGIN
        } else {
            authCommand.value = Authentication.LOGOUT
        }
    }

    fun onStarClickedUserNotConnected() {
        authCommand.value = Authentication.LOGIN_FROM_STAR
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

    // ************************* Starred sessions **********************************
    fun updateSessionListWithStarredSessions() {
        repository.getStarredSessions(OnSuccessListener { documentSnapshot ->
            val userInFirebase = documentSnapshot.toObject(User::class.java)
            userInFirebase?.let { userInFirebase ->
                if (userInFirebase.starredSessions.isEmpty()) return@OnSuccessListener

                userInFirebase.starredSessions.forEach {
                    sessions[it.toInt()].starred = true
                }
                filterList()
            }
        })
    }

    val showSnackBarForStarringCommand: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun starOrUnstarSession(sessionId: String, toStar: Boolean) {
        repository.starOrUnstarSession(sessionId, toStar)
        sessions[sessionId.toInt()].starred = toStar
        showSnackBarForStarringCommand.value = toStar
    }

    fun unstarAllSessions() {
        sessions.forEach {
            it.starred = false
        }
        filterList()
    }

    init {
        App.component.inject(this)

        FirebaseAuth.getInstance().currentUser?.let {
            showUserPhoto(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}

// ********************* Sessions' info to display in the list. ****************************
class SessionInfoForList(
    val id: String,
    val title: String,
    val liveStreamed: Boolean,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val location: String,
    val tags: List<Tag>,
    var day: Int,
    var starred: Boolean
)