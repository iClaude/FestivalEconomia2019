package iclaude.festivaleconomia2019.ui.sessioninfo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import iclaude.festivaleconomia2019.databinding.FragmentSessionInfoBinding
import iclaude.festivaleconomia2019.ui.utils.EventObserver


class SessionInfoFragment : Fragment() {

    private lateinit var sessionId: String
    private lateinit var viewModel: SessionInfoViewModel
    private lateinit var binding: FragmentSessionInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionId = SessionInfoFragmentArgs.fromBundle(arguments!!).sessionId
        viewModel = ViewModelProviders.of(this).get(SessionInfoViewModel::class.java).apply {
            sessionId = this@SessionInfoFragment.sessionId

            eventDataFromRepoLive.observe(this@SessionInfoFragment, Observer {
                viewModel.loadSessionInfo()
            })

            startYoutubeVideoEvent.observe(this@SessionInfoFragment, EventObserver {

            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionInfoBinding.inflate(inflater, container, false).apply {
            viewModel = this@SessionInfoFragment.viewModel
        }

        return binding.root
    }

}


