package iclaude.festivaleconomia2019.ui.details.organizer

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import iclaude.festivaleconomia2019.databinding.FragmentOrganizerBinding
import iclaude.festivaleconomia2019.ui.sessions.SessionListViewModel
import iclaude.festivaleconomia2019.ui.utils.EventObserver

class OrganizerFragment : Fragment() {

    private lateinit var viewModel: OrganizerViewModel
    private lateinit var sessionListViewModel: SessionListViewModel
    private lateinit var binding: FragmentOrganizerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.window?.statusBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(OrganizerViewModel::class.java)
        sessionListViewModel = activity?.run {
            ViewModelProviders.of(this).get(SessionListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val idOrganizer = OrganizerFragmentArgs.fromBundle(arguments!!).organizerId

        viewModel.apply {
            organizerId = idOrganizer

            eventDataFromRepoLive.observe(this@OrganizerFragment, Observer {
                viewModel.loadOrganizerInfo()
            })

            organizerInfoLoadedEvent.observe(this@OrganizerFragment, EventObserver { info ->
                binding.apply {
                    organizerData = info
                }
                // viewModel.findStarredSessions()
            })
        }

        binding = FragmentOrganizerBinding.inflate(inflater, container, false).apply {
            viewModel = this@OrganizerFragment.viewModel
        }

        return binding.root
    }

}
