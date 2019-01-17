package iclaude.festivaleconomia2019.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import kotlinx.android.synthetic.main.fragment_container_sessions.*
import javax.inject.Inject


class ContainerSessionsFragment : Fragment() {

    @Inject
    lateinit var repository: EventDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_container_sessions, container, false)

        repository.eventDataLive.observe(this, Observer { eventData ->
            textView.text = eventData.toString()
        })

        return root

    }


}
