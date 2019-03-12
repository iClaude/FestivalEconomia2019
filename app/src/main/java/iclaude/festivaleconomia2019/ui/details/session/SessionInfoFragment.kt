package iclaude.festivaleconomia2019.ui.details.session


import android.content.Intent
import android.net.Uri
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

            sessionInfoLoadedEvent.observe(this@SessionInfoFragment, EventObserver { info ->
                binding?.apply {
                    sessionData = info
                }
            })

            startYoutubeVideoEvent.observe(this@SessionInfoFragment, EventObserver {
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(it)
                }
                if (intent.resolveActivity(context?.packageManager) != null) {
                    startActivity(intent)
                }
            })

            navigateBackEvent.observe(this@SessionInfoFragment, EventObserver {
                activity?.finish()
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


