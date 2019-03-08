package iclaude.festivaleconomia2019.ui.sessions

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.edit
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
import iclaude.festivaleconomia2019.ui.sessions.SessionListViewModel.Authentication.*
import iclaude.festivaleconomia2019.ui.utils.EventObserver
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
                    LOGIN_REQUEST -> requestLogin()
                    LOGIN_CONFIRMED -> logIn()
                    LOGOUT_REQUEST -> requestLogout()
                    LOGOUT_CONFIRMED -> logOut()
                }
            })
            showSnackBarForStarringEvent.observe(this@SessionContainerFragment, EventObserver { toStar ->
                val pref = activity?.getPreferences(Context.MODE_PRIVATE)
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
        }
    }

    // User login/logout.

    // Request user confirmation for login.
    private fun requestLogin() {
        val posButtonListener = DialogInterface.OnClickListener { dialog, which ->
            viewModel.confirmLogin()
            dialog?.dismiss()
        }
        val negButtonListener = DialogInterface.OnClickListener { dialog, which -> dialog?.dismiss() }
        showAlertDialog(
            R.string.login_confirm_title, R.string.login_confirm_msg, R.string.login_confirm_accept,
            R.string.login_confirm_cancel, posButtonListener, negButtonListener
        )
    }

    // User has confirmed login.
    private fun logIn() {
        // Choose authentication providers.
        val providers = viewModel.getLoginProviders()

        // Create and launch sign-in intent.
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.AppTheme)
                .setLogo(R.mipmap.ic_launcher_round)
                .build(),
            RC_SIGN_IN
        )
    }

    // Request user confirmation for logout.
    private fun requestLogout() {
        val posButtonListener = DialogInterface.OnClickListener { dialog, which ->
            viewModel.confirmLogout()
            dialog?.dismiss()
        }
        val negButtonListener = DialogInterface.OnClickListener { dialog, which -> dialog?.dismiss() }
        showAlertDialog(
            R.string.logout_dialog_title, R.string.logout_dialog_msg, R.string.logout_dialog_accept,
            R.string.logout_dialog_cancel, posButtonListener, negButtonListener
        )
    }

    // User has confirmed logout.
    private fun logOut() {
        AuthUI.getInstance()
            .signOut(context!!)
            .addOnCompleteListener {
                viewModel.onUserLoggedOut()
            }
    }

    private fun showAlertDialog(
        @StringRes title: Int, @StringRes msg: Int, @StringRes posButton: Int,
        @StringRes negButton: Int, posButtonListener: DialogInterface.OnClickListener,
        negButtonListener: DialogInterface.OnClickListener
    ) {
        MaterialAlertDialogBuilder(context!!)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(posButton, posButtonListener)
            .setNegativeButton(negButton, negButtonListener)
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
                    viewModel.onUserLoggedIn(it)
                }
            } else {
                Snackbar.make(
                    fabFilter, getString(R.string.login_error, response?.error?.errorCode ?: 0),
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

        override fun getCount() = numDays

        override fun getItem(position: Int): Fragment = SessionListFragment.newInstance(position)

        override fun getPageTitle(position: Int): CharSequence {
            return tabsInfo[position].label
        }
    }
}
