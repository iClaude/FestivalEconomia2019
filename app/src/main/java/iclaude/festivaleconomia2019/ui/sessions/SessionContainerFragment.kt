package iclaude.festivaleconomia2019.ui.sessions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.bottomsheet.BottomSheetBehavior.from
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.FragmentSessionContainerBinding
import iclaude.festivaleconomia2019.ui.sessions.SessionListViewModel.Authentication
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
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup ViewPager and TabLayout
        viewModel.repository.eventDataLive.observe(this, Observer { eventData ->
            with(viewModel) {
                dataLoadedObs.set(true)
                loadInfoList(eventData)
            }

            viewPager.adapter =
                SessionsAdapter(
                    childFragmentManager,
                    numberOfDays(context, eventData.sessions),
                    daysLabels(context, eventData.sessions).also {
                        it.add(DayLabel(label = getString(R.string.sessions_agenda)))
                    }
                )
        })
        tabs.setupWithViewPager(viewPager)

        // filter sheet
        bottomSheetBehavior = from(bottomSheet)
        viewModel.run {
            titleHeaderAlphaObs.set(0f)
            scrollYObs.set(0)
            changeFilterSheetState(false)
            changeFilterSheetStateCommand.observe(this@SessionContainerFragment, Observer {
                bottomSheetBehavior.state = it
            })
            removeFilterSheetCommand.observe(this@SessionContainerFragment, Observer {
                bottomSheetBehavior.run {
                    isHideable = true
                    state = STATE_HIDDEN
                }

            })
            authCommand.observe(this@SessionContainerFragment, Observer { command ->
                when (command) {
                    Authentication.LOGIN -> logIn()
                    else -> logOut()
                }
            })
        }
    }

    private fun logIn() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.TwitterBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.AppTheme)
                .setLogo(R.drawable.event_logo_login)
                .build(),
            RC_SIGN_IN
        )
    }


    private fun logOut() {
        MaterialAlertDialogBuilder(context!!)
            .setTitle(R.string.logout_dialog_title)
            .setMessage(R.string.logout_dialog_msg)
            .setPositiveButton(R.string.logout_dialog_accept) { dialog, _ ->
                AuthUI.getInstance()
                    .signOut(context!!)
                    .addOnCompleteListener {
                        viewModel.run {
                            userImageUriObs.set(null)
                            unstarAllSessions()
                        }
                    }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.logout_dialog_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {
                    with(viewModel) {
                        showUserPhoto(it)
                        repository.addUser(it)
                        updateSessionListWithStarredSessions()
                    }
                }
            } else {
                Snackbar.make(
                    fabFilter, getString(R.string.login_error, response?.getError()?.getErrorCode() ?: 0),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    inner class SessionsAdapter(
        fm: FragmentManager,
        private val numDays: Int,
        private val tabsInfo: MutableList<DayLabel>
    ) : FragmentPagerAdapter(fm) {

        override fun getCount() = numDays + 1

        override fun getItem(position: Int): Fragment {
            return when (position) {
                in 0 until numDays -> {
                    SessionListFragment.newInstance(position)
                }
                else -> AgendaFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return tabsInfo[position].label
        }
    }
}
