package iclaude.festivaleconomia2019.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import iclaude.festivaleconomia2019.model.data_classes.Location
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import javax.inject.Inject

class MapViewModel : ViewModel() {
    init {
        App.component.inject(this)
    }

    @Inject
    lateinit var mRepository: EventDataRepository

    val eventDataLive: LiveData<List<Location>>
        get() {
            return Transformations.map(mRepository.eventDataLive) { eventData ->
                eventData.locations
            }
        }


}