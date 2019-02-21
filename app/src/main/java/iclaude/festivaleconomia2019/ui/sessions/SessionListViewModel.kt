package iclaude.festivaleconomia2019.ui.sessions

import android.app.Application
import android.widget.CompoundButton
import androidx.databinding.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.data_classes.Tag
import iclaude.festivaleconomia2019.model.data_classes.hasSessionUrl
import iclaude.festivaleconomia2019.model.data_classes.hasYoutubeUrl
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import iclaude.festivaleconomia2019.ui.sessions.filters.Filter
import iclaude.festivaleconomia2019.ui.sessions.filters.hasTags
import iclaude.festivaleconomia2019.ui.sessions.filters.isFilterSet
import iclaude.festivaleconomia2019.ui.sessions.filters.isStarred
import iclaude.festivaleconomia2019.ui.utils.SingleLiveEvent
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject

class SessionListViewModel(val context: Application) : AndroidViewModel(context) {
    private lateinit var sessionsInfo: List<SessionsDisplayInfo>

    var filterSelected: MutableLiveData<Filter> = MutableLiveData()
    val isFilterTaggedObs: ObservableBoolean = ObservableBoolean(false)
    val isFilterStarredObs: ObservableBoolean = ObservableBoolean(false)
    val filterTagsObs: ObservableList<Tag> = ObservableArrayList()

    fun updateFilter(filter: Filter?) {
        filterSelected.value = filter
        isFilterTaggedObs.set(filter?.hasTags() ?: false)
        isFilterStarredObs.set(filter?.isStarred() ?: false)
        filterTagsObs.apply {
            clear()
            filter?.let {
                this.addAll(it.tags)
            }
        }
    }

    fun clearFilters() {
        filterSelected.value = Filter()
        isFilterTaggedObs.set(false)
        isFilterStarredObs.set(false)
        clearTagsObs.set(clearTagsObs.get() + 1)
        filterTagsObs.clear()
    }

    fun clearFiltersAndCollapse() {
        clearFilters()
        removeFilterSheetCommand.call()
    }

    init {
        App.component.inject(this)
        updateFilter(Filter())
    }

    @Inject
    lateinit var repository: EventDataRepository

    var dataLoadedObs: ObservableBoolean = ObservableBoolean(false)

    var sessionsFilteredObs: ObservableInt = ObservableInt(0)
    val sessionsInfoFilteredLive: LiveData<List<SessionsDisplayInfo>>
        get() = Transformations.switchMap(filterSelected) { filter ->
            val filteredList = sessionsInfo.toMutableList()

            // filter by tags and starred
            if (filter.isFilterSet()) {
                filteredList.retainAll {
                    it.tags.intersect(filter.tags).isNotEmpty() || (filter.isStarred() && it.starred)
                }
            }

            sessionsFilteredObs.set(filteredList.size)
            MutableLiveData<List<SessionsDisplayInfo>>().apply { value = filteredList }
        }

    // load original list of sessions when data is loaded: triggered from SessionContainerFragment
    fun loadInfoList(eventData: EventData) {
        sessionsInfo = eventData.sessions.map { session ->
            SessionsDisplayInfo(
                session.id, session.title,
                session.hasSessionUrl() || (session.hasYoutubeUrl()),
                session.startTimestamp,
                session.endTimestamp,
                eventData.locations[session.location.toInt()].name,
                session.tags.map {
                    eventData.tags[it.toInt()]
                },
                0,
                false
            )
        }

        filterByDays(sessionsInfo)
        loadAllTags()
    }

    private fun filterByDays(sessions: List<SessionsDisplayInfo>) {
        var i = 0
        var baseDay = timestampToZonedDateTime(sessions[0].startTimestamp, context)
        for (session in sessions) {
            val curDay = timestampToZonedDateTime(session.startTimestamp, context)
            if (ChronoUnit.DAYS.between(baseDay, curDay) > 0) {
                session.day = ++i
                baseDay = curDay
            } else {
                session.day = i
            }
        }
    }

    // Data for filter sheet.
    val tagsObs: ObservableList<Tag> = ObservableArrayList()
    private fun loadAllTags() {
        val tags = repository.eventDataLive.value?.tags
        tagsObs.clear()
        tagsObs.addAll(tags ?: return)
    }

    val clearTagsObs: ObservableInt = ObservableInt(0)

    val titleHeaderAlphaObs: ObservableFloat = ObservableFloat(0f)

    fun chipStarredCheckedChanged(compoundButton: CompoundButton, isChecked: Boolean) {
        val filter = filterSelected.value
        filter?.starred = isChecked
        updateFilter(filter)
    }

    // Filter sheet commands.
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
}

class SessionsDisplayInfo(
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