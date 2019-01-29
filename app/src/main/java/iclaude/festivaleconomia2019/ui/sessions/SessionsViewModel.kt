package iclaude.festivaleconomia2019.ui.sessions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import iclaude.festivaleconomia2019.model.data_classes.Location
import iclaude.festivaleconomia2019.model.data_classes.Session
import iclaude.festivaleconomia2019.model.data_classes.Tag
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import javax.inject.Inject

class SessionsViewModel : ViewModel() {
    private val TAG = "MY_BINDING_ADAPTER"

    init {
        App.component.inject(this)
    }

    @Inject
    lateinit var mRepository: EventDataRepository

    private var sessions: List<Session> = mutableListOf()
    private var locations: List<Location> = mutableListOf()
    private var tags: List<Tag> = mutableListOf()

    private val _dataLoadedLive: MutableLiveData<Boolean> = MutableLiveData()
    val dataLoadedLive: LiveData<Boolean>
        get() = _dataLoadedLive

    fun loadData(sessionsList: List<Session>, locationsList: List<Location>, tagsList: List<Tag>) {
        sessions = sessionsList
        locations = locationsList
        tags = tagsList
        _dataLoadedLive.value = true
    }
}