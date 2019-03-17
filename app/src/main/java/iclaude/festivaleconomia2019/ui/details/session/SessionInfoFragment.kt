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
import com.google.android.material.internal.CheckableImageButton
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.FragmentSessionInfoBinding
import iclaude.festivaleconomia2019.ui.utils.EventObserver
import kotlinx.android.synthetic.main.fragment_session_info.*
import kotlinx.android.synthetic.main.fragment_session_info_content.*


class SessionInfoFragment : Fragment() {

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
        val idSession = SessionInfoFragmentArgs.fromBundle(arguments!!).sessionId
        viewModel.apply {
            sessionId = idSession

            eventDataFromRepoLive.observe(this@SessionInfoFragment, Observer {
                viewModel.loadSessionInfo()
            })

            sessionInfoLoadedEvent.observe(this@SessionInfoFragment, EventObserver { info ->
                binding.apply {
                    sessionData = info
                }
                if (info.hasRelatedSessions()) viewModel.findStarredSessions()
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
                        popExit = R.anim.slide_out_right
                    }
                }
                findNavController().navigate(R.id.sessionInfoFragment, bundleOf("sessionId" to it), options)
            })

            starredSessionsLive.observe(this@SessionInfoFragment, Observer { starredSessionIds ->
                starredSessionIds.forEach { id ->
                    val starredSessionView: View? = llRelatedEvents.findViewWithTag<View>(id)
                    starredSessionView?.findViewById<CheckableImageButton>(R.id.cibBookmark).apply {
                        this?.isChecked = true
                    }
                }
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


