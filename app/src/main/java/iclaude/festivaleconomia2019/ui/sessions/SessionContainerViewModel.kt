package iclaude.festivaleconomia2019.ui.sessions

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import javax.inject.Inject

class SessionContainerViewModel : ViewModel() {
    init {
        App.component.inject(this)
    }

    @Inject
    lateinit var repository: EventDataRepository

    var dataLoadedObs: ObservableBoolean = ObservableBoolean(false)
}