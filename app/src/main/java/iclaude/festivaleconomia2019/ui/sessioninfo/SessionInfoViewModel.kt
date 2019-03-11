package iclaude.festivaleconomia2019.ui.sessioninfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import javax.inject.Inject

class SessionInfoViewModel : ViewModel() {
    @Inject
    lateinit var repository: EventDataRepository
    val eventDataFromRepoLive = Transformations.switchMap(repository.eventDataLive) {
        MutableLiveData<EventData>().apply {
            value = it
        }
    }
}