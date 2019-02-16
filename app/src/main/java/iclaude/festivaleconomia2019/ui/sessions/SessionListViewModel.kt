package iclaude.festivaleconomia2019.ui.sessions

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.data_classes.Tag
import iclaude.festivaleconomia2019.model.data_classes.hasSessionUrl
import iclaude.festivaleconomia2019.model.data_classes.hasYoutubeUrl
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import iclaude.festivaleconomia2019.ui.sessions.filters.Filter
import iclaude.festivaleconomia2019.ui.sessions.filters.hasTags
import iclaude.festivaleconomia2019.ui.sessions.filters.isStarred
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject

class SessionListViewModel(val context: Application) : AndroidViewModel(context) {
    private lateinit var sessionsInfo: List<SessionsDisplayInfo>

    var filterSelected: MutableLiveData<Filter> = MutableLiveData()

    init {
        App.component.inject(this)
        filterSelected.value = Filter()
    }

    @Inject
    lateinit var repository: EventDataRepository

    var dataLoadedObs: ObservableBoolean = ObservableBoolean(false)
    var sessionsFilteredObs: ObservableInt = ObservableInt(0)

    val sessionsInfoFilteredLive: LiveData<List<SessionsDisplayInfo>>
        get() = Transformations.switchMap(filterSelected) { filter ->
            val filteredList = sessionsInfo.toMutableList()

            // filter by stars
            if (filter.isStarred()) {
                filteredList.retainAll {
                    it.starred
                }
            }

            // filter by tags
            if (filter.hasTags()) {
                filteredList.retainAll {
                    it.tags.intersect(filter.tags).isNotEmpty()
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