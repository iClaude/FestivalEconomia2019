package iclaude.festivaleconomia2019.ui.details.organizer

import androidx.databinding.ObservableFloat
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

class OrganizerViewModel : ViewModel() {
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

    // AppBar info.
    val appBarCollapsedPercentageObs = ObservableFloat(0f)
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