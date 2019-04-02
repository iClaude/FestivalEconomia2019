package iclaude.festivaleconomia2019.ui.sessions

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.bottomsheet.BottomSheetBehavior.from
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.FragmentSessionContainerBinding
import iclaude.festivaleconomia2019.ui.login.LoginFlow.Authentication
import iclaude.festivaleconomia2019.ui.login.LoginManager
import iclaude.festivaleconomia2019.ui.utils.DayLabel
import iclaude.festivaleconomia2019.ui.utils.EventObserver
import iclaude.festivaleconomia2019.ui.utils.daysLabels
import iclaude.festivaleconomia2019.ui.utils.numberOfDays
import kotlinx.android.synthetic.main.fragment_session_container.*
import kotlinx.android.synthetic.main.fragment_session_container_appbar.*

const val RC_SIGN_IN = 66

class SessionContainerFragment : Fragment() {

    private lateinit var viewModel: SessionListViewModel
    private lateinit var binding: FragmentSessionContainerBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(SessionListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionContainerBinding.inflate(inflater, container, false).apply {
            viewModel = this@SessionContainerFragment.viewModel
            lifecycleOwner = this@SessionContainerFragment.viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup ViewPager and TabLayout
        viewModel.eventDataFromRepoLive.observe(this, Observer { eventData ->
            with(viewModel) {
                loadInfoList(eventData)
            }

            viewPager.adapter =
                SessionsAdapter(
                    childFragmentManager,
                    numberOfDays(context, eventData.sessions),
                    daysLabels(context, eventData.sessions)
                )
        })
        tabs.setupWithViewPager(viewPager)

        // filter sheet
        bottomSheetBehavior = from(bottomSheet)
        viewModel.run {
            titleHeaderAlphaObs.set(0f)
            scrollYObs.set(0)
            changeFilterSheetState(false)

            changeFilterSheetStateEvent.observe(this@SessionContainerFragment, EventObserver {
                bottomSheetBehavior.state = it
            })

            removeFilterSheetEvent.observe(this@SessionContainerFragment, EventObserver {
                bottomSheetBehavior.run {
                    isHideable = true
                    state = STATE_HIDDEN
                }
            })

            authEvent.observe(this@SessionContainerFragment, EventObserver { command ->
                when (command) {
                    Authentication.LOGIN_REQUEST -> LoginManager.requestLogin(activity!!, viewModel)
                    Authentication.LOGIN_CONFIRMED -> LoginManager.logIn(this@SessionContainerFragment, viewModel)
                    Authentication.LOGOUT_REQUEST -> LoginManager.requestLogout(activity!!, viewModel)
                    Authentication.LOGOUT_CONFIRMED -> LoginManager.logOut(activity!!, viewModel)
                }
            })

            showSnackBarForStarringEvent.observe(this@SessionContainerFragment, EventObserver { toStar ->
                val pref = PreferenceManager.getDefaultSharedPreferences(context)
                val showSnackbar = pref?.getBoolean("starring_show_snackbar", true) ?: true
                if (!showSnackbar) return@EventObserver
                val msgId = if (toStar) R.string.starred else R.string.unstarred
                Snackbar.make(fabFilter, msgId, Snackbar.LENGTH_SHORT).run {
                    setAction(R.string.starred_unstarred_not_show) {
                        pref?.edit { putBoolean("starring_show_snackbar", false) }
                    }
                    show()
                }
            })

            goToSessionEvent.observe(this@SessionContainerFragment, EventObserver {
                SessionContainerFragmentDirections.actionContainerSessionsFragmentToDetailsGraph(it).run {
                    findNavController().navigate(this)
                }
            })

            loginDataUpdateEvent.observe(this@SessionContainerFragment, EventObserver {
                FirebaseAuth.getInstance().currentUser?.let {
                    viewModel.onUserLoggedIn(it)
                }
            })
        }
    }

    // User login result.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        LoginManager.loginResult(requestCode, resultCode, data, fabFilter, activity!!, viewModel)
    }

    inner class SessionsAdapter(
        fm: FragmentManager,
        private val numDays: Int,
        private val tabsInfo: MutableList<DayLabel>
    ) : FragmentPagerAdapter(fm) {

        override fun getCount() = numDays

        override fun getItem(position: Int): Fragment = SessionListFragment.newInstance(position)

        override fun getPageTitle(position: Int): CharSequence {
            return tabsInfo[position].label
        }
    }
}
