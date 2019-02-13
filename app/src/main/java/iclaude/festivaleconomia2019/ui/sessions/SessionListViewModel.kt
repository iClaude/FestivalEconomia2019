package iclaude.festivaleconomia2019.ui.sessions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.data_classes.Tag
import iclaude.festivaleconomia2019.model.data_classes.hasSessionUrl
import iclaude.festivaleconomia2019.model.data_classes.hasYoutubeUrl
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import iclaude.festivaleconomia2019.ui.sessions.filters.Filter
import javax.inject.Inject

class SessionListViewModel : ViewModel() {
    private var origList: List<SessionsDisplayInfo>

    init {
        App.component.inject(this)
        // load original static list only once; eventDataLive has already been obtained by container Fragment (no need to observe it)
        origList = loadInfoList(repository.eventDataLive.value)
    }

    @Inject
    lateinit var repository: EventDataRepository

    var filterSelected: MutableLiveData<Filter> = MutableLiveData()

    val sessionsInfoFilteredLive: LiveData<List<SessionsDisplayInfo>>
        get() = Transformations.switchMap(filterSelected) { filter ->
            val start = startOfDay(filter.day!!).toInstant().toEpochMilli()
            val end = endOfDay(filter.day!!).toInstant().toEpochMilli()

            // filter by date
            var filteredList = origList.filter {
                it.startTimestamp >= start && it.endTimestamp <= end
            }
            // filter by stars
            if (filter.starred) {
                filteredList = filteredList.filter {
                    it.starred
                }
            }
            // filter by tags
            if (filter.tags.isNotEmpty()) {
                filteredList = filteredList.filter {
                    for (tagFilter in filter.tags) {
                        for (tagSession in it.tags) {
                            if (tagFilter == tagSession.id) true
                        }
                    }
                    false
                }
            }

            val result = MutableLiveData<List<SessionsDisplayInfo>>()
            result.value = filteredList
            result
        }

    private fun loadInfoList(eventData: EventData?): List<SessionsDisplayInfo> {
        if (eventData == null) return emptyList()
        return eventData.sessions.map { session ->
            SessionsDisplayInfo(
                session.id, session.title,
                session.hasSessionUrl() || (session.hasYoutubeUrl()),
                session.startTimestamp,
                session.endTimestamp,
                eventData.locations[session.location.toInt()].name,
                session.tags.map {
                    eventData.tags[it.toInt()]
                },
                false
            )
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
    val starred: Boolean
)