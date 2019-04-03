package iclaude.festivaleconomia2019.ui.details.organizer

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import androidx.transition.TransitionInflater
import com.google.android.material.internal.CheckableImageButton
import com.google.android.material.snackbar.Snackbar
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.FragmentOrganizerBinding
import iclaude.festivaleconomia2019.ui.login.LoginFlow
import iclaude.festivaleconomia2019.ui.login.LoginManager
import iclaude.festivaleconomia2019.ui.sessions.SessionListViewModel
import iclaude.festivaleconomia2019.ui.utils.EventObserver
import iclaude.festivaleconomia2019.ui.utils.postponeEnterTransition
import kotlinx.android.synthetic.main.fragment_organizer_content.*
import kotlinx.android.synthetic.main.fragment_session_info.*

class OrganizerFragment : Fragment() {

    private lateinit var viewModel: OrganizerViewModel
    private lateinit var sessionListViewModel: SessionListViewModel
    private lateinit var binding: FragmentOrganizerBinding
    private lateinit var idOrganizer: String


    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.window?.statusBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)

        with(TransitionInflater.from(context).inflateTransition(R.transition.changebounds_with_arcmotion)) {
            duration = 500 // bug in source code?
            sharedElementEnterTransition = this
            sharedElementReturnTransition = this // currently not working
        }

        viewModel = ViewModelProviders.of(this).get(OrganizerViewModel::class.java)
        sessionListViewModel = activity?.run {
            ViewModelProviders.of(this).get(SessionListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Delay the Activity enter transition until speaker image has loaded
        activity?.postponeEnterTransition(500L)

        idOrganizer = OrganizerFragmentArgs.fromBundle(arguments!!).organizerId

        viewModel.apply {
            organizerId = idOrganizer

            eventDataFromRepoLive.observe(this@OrganizerFragment, Observer {
                viewModel.loadOrganizerInfo()
            })

            hasProfileImageEvent.observe(this@OrganizerFragment, EventObserver { hasProfile ->
                if (!hasProfile) activity?.startPostponedEnterTransition()
            })

            organizerInfoLoadedEvent.observe(this@OrganizerFragment, EventObserver { info ->
                binding.apply {
                    organizerData = info
                }
                viewModel.findStarredSessions()
            })

            goToSessionEvent.observe(this@OrganizerFragment, EventObserver {
                val options = navOptions {
                    anim {
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_right
                    }
                }
                findNavController().navigate(R.id.sessionInfoFragment, bundleOf("sessionId" to it), options)
            })

            starredSessionsLive.observe(this@OrganizerFragment, Observer { starredSessionIds ->
                // List of related sessions.
                starredSessionIds.forEach { id ->
                    val starredSessionView: View? = llOrganizerEvents?.findViewWithTag(id)
                    starredSessionView?.findViewById<CheckableImageButton>(R.id.cibBookmark).apply {
                        this?.isChecked = true
                    }
                }
            })

            showSnackBarForStarringEvent.observe(this@OrganizerFragment, EventObserver { toStar ->
                val pref = PreferenceManager.getDefaultSharedPreferences(context)
                val showSnackbar = pref?.getBoolean("starring_show_snackbar", true) ?: true
                if (!showSnackbar) return@EventObserver
                val msgId = if (toStar) R.string.starred else R.string.unstarred
                Snackbar.make(tvRelatedEventsTitle, msgId, Snackbar.LENGTH_SHORT).run {
                    setAction(R.string.starred_unstarred_not_show) {
                        pref?.edit { putBoolean("starring_show_snackbar", false) }
                    }
                    show()
                }
            })

            authEvent.observe(this@OrganizerFragment, EventObserver { command ->
                when (command) {
                    LoginFlow.Authentication.LOGIN_REQUEST -> LoginManager.requestLogin(activity!!, viewModel)
                    LoginFlow.Authentication.LOGIN_CONFIRMED -> LoginManager.logIn(this@OrganizerFragment, viewModel)
                }
            })

            /* If the user logs in-out or stars/unstars sessions in this Fragment,
               SessionListFragment should also be updated (showing avatar and updating starred
               sessions). */
            loginOperationsEvent.observe(this@OrganizerFragment, EventObserver {
                sessionListViewModel.loginDataNeedsUpdate()
            })
        }

        binding = FragmentOrganizerBinding.inflate(inflater, container, false).apply {
            viewModel = this@OrganizerFragment.viewModel
            lifecycleOwner = this@OrganizerFragment.viewLifecycleOwner

            avatarListener = object : ImageLoadListener {
                override fun onImageLoaded() {
                    activity?.startPostponedEnterTransition()
                }

                override fun onImageLoadFailed() {
                    activity?.startPostponedEnterTransition()
                }
            }
        }

        // retrieve the unique transition name for the organizer's avatar (there could be more organizers for the same event and we must know which avatar to animate)
        binding.root.findViewById<ImageView>(R.id.ivAvatar).transitionName += idOrganizer

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setupWithNavController(findNavController())
    }

    // User login result.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        LoginManager.loginResult(requestCode, resultCode, data, llOrganizerEvents, activity!!, viewModel)
    }

}
