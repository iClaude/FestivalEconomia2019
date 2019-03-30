package iclaude.festivaleconomia2019.ui.details.session


import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.CheckableImageButton
import com.google.android.material.snackbar.Snackbar
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.FragmentSessionInfoBinding
import iclaude.festivaleconomia2019.ui.login.LoginFlow.Authentication
import iclaude.festivaleconomia2019.ui.login.LoginManager
import iclaude.festivaleconomia2019.ui.map.MapFragmentDirections
import iclaude.festivaleconomia2019.ui.sessions.SessionListViewModel
import iclaude.festivaleconomia2019.ui.utils.EventObserver
import iclaude.festivaleconomia2019.ui.utils.sessionInfoTimeDetails
import kotlinx.android.synthetic.main.fragment_session_info.*
import kotlinx.android.synthetic.main.fragment_session_info_content.*
import kotlinx.android.synthetic.main.item_organizer.*


class SessionInfoFragment : Fragment() {

    private lateinit var viewModel: SessionInfoViewModel
    private lateinit var sessionListViewModel: SessionListViewModel
    private lateinit var binding: FragmentSessionInfoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.window?.statusBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(this).get(SessionInfoViewModel::class.java)
        sessionListViewModel = activity?.run {
            ViewModelProviders.of(this).get(SessionListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
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
                viewModel.findStarredSessions()
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

            goToOrganizerEvent.observe(this@SessionInfoFragment, EventObserver { organizerId ->
                val extras =
                    FragmentNavigatorExtras(ivAvatar to "${context!!.getString(R.string.speaker_headshot_transition)}${organizerId}") // use a unique transition name for each avatar in the list
                val action =
                    SessionInfoFragmentDirections.actionSessionInfoFragmentToOrganizerFragment(organizerId).run {
                        findNavController().navigate(this, extras)
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
                findNavController().navigate(R.id.sessionInfoFragment, bundleOf("sessionId" to it), options)
            })

            starredSessionsLive.observe(this@SessionInfoFragment, Observer { starredSessionIds ->
                // List of related sessions.
                starredSessionIds.forEach { id ->
                    val starredSessionView: View? = llRelatedEvents?.findViewWithTag(id)
                    starredSessionView?.findViewById<CheckableImageButton>(R.id.cibBookmark).apply {
                        this?.isChecked = true
                    }
                }
            })

            showSnackBarForStarringEvent.observe(this@SessionInfoFragment, EventObserver { toStar ->
                val pref = PreferenceManager.getDefaultSharedPreferences(context)
                val showSnackbar = pref?.getBoolean("starring_show_snackbar", true) ?: true
                if (!showSnackbar) return@EventObserver
                val msgId = if (toStar) R.string.starred else R.string.unstarred
                Snackbar.make(cibStar, msgId, Snackbar.LENGTH_SHORT).run {
                    setAction(R.string.starred_unstarred_not_show) {
                        pref?.edit { putBoolean("starring_show_snackbar", false) }
                    }
                    show()
                }
            })

            authEvent.observe(this@SessionInfoFragment, EventObserver { command ->
                when (command) {
                    Authentication.LOGIN_REQUEST -> LoginManager.requestLogin(activity!!, viewModel)
                    Authentication.LOGIN_CONFIRMED -> LoginManager.logIn(this@SessionInfoFragment, viewModel)
                }
            })

            /* If the user logs in-out or stars/unstars sessions in this Fragment,
               SessionListFragment should also be updated (showing avatar and updating starred
               sessions). */
            loginOperationsEvent.observe(this@SessionInfoFragment, EventObserver {
                sessionListViewModel.loginDataNeedsUpdate()
            })
        }

        binding = FragmentSessionInfoBinding.inflate(inflater, container, false).apply {
            viewModel = this@SessionInfoFragment.viewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(toolbar) {
            setupWithNavController(findNavController())
            inflateMenu(R.menu.session_info)

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_location -> {
                        val action = MapFragmentDirections.actionGlobalMapFragment().apply {
                            locationId = viewModel.locationId
                        }
                        findNavController().navigate(action)
                        (activity?.findViewById(R.id.bottomNav) as BottomNavigationView).menu.findItem(R.id.mapFragment)
                            .isChecked = true

                        true
                    }
                    R.id.action_share -> {
                        startActivity(
                            Intent.createChooser(
                                createShareIntent(),
                                getString(R.string.session_info_share_msg)
                            )
                        )
                        true
                    }
                    else -> super.onOptionsItemSelected(item)
                }
            }
        }
    }

    // User login result.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        LoginManager.loginResult(requestCode, resultCode, data, cibStar, activity!!, viewModel)
    }

    private fun createShareIntent(): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, createShareString())
        }
    }

    private fun createShareString(): String {
        val sessionInfo = viewModel.sessionInfo
        val str = "${getString(R.string.session_info_share)}\n\n${sessionInfo.title}\n${sessionInfoTimeDetails(
            context,
            sessionInfo.startTimestamp, sessionInfo.endTimestamp
        )}\n${sessionInfo.location}\n\n\"${sessionInfo.description.take(
            120
        )}\"...\n\nUrl: ${if (sessionInfo.sessionUrl != null) sessionInfo.sessionUrl else "-"}"

        return str
    }

}


