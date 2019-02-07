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
import org.threeten.bp.ZonedDateTime
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

    var daySelected: MutableLiveData<ZonedDateTime> = MutableLiveData()

    val sessionsInfoFilteredLive: LiveData<List<SessionsDisplayInfo>>
        get() = Transformations.switchMap(daySelected) { dayChosen ->
            val start = startOfDay(dayChosen).toInstant().toEpochMilli()
            val end = endOfDay(dayChosen).toInstant().toEpochMilli()

            val filteredList = origList.filter {
                it.startTimestamp >= start && it.endTimestamp <= end
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
                session.tags2.map {
                    eventData.tags[it.toInt()]
                }
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
    val tags: List<Tag>
)