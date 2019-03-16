package iclaude.festivaleconomia2019.ui.details.session


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.FragmentSessionInfoBinding
import iclaude.festivaleconomia2019.ui.utils.EventObserver
import kotlinx.android.synthetic.main.fragment_session_info.*


class SessionInfoFragment : Fragment() {

    private lateinit var sessionId: String
    private lateinit var viewModel: SessionInfoViewModel
    private lateinit var binding: FragmentSessionInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(SessionInfoViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sessionId = SessionInfoFragmentArgs.fromBundle(arguments!!).sessionId
        viewModel.apply {
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

            goToSessionEvent.observe(this@SessionInfoFragment, EventObserver {
                val options = navOptions {
                    anim {
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_right
                    }
                }
                findNavController().navigate(R.id.sessionInfoFragment2, bundleOf("sessionId" to it), options)
            })
        }

        binding = FragmentSessionInfoBinding.inflate(inflater, container, false).apply {
            viewModel = this@SessionInfoFragment.viewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setupWithNavController(findNavController())
    }
}


