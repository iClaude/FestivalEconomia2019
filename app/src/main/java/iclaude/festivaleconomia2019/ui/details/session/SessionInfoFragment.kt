package iclaude.festivaleconomia2019.ui.details.session


import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.internal.CheckableImageButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.FragmentSessionInfoBinding
import iclaude.festivaleconomia2019.ui.details.session.SessionInfoViewModel.Authentication
import iclaude.festivaleconomia2019.ui.sessions.RC_SIGN_IN
import iclaude.festivaleconomia2019.ui.utils.EventObserver
import kotlinx.android.synthetic.main.fragment_session_info.*
import kotlinx.android.synthetic.main.fragment_session_info_content.*
import kotlinx.android.synthetic.main.item_session_related.*


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
                        exit = R.anim.slide_out_left
                        popEnter = R.anim.slide_in_left
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

            showSnackBarForStarringEvent.observe(this@SessionInfoFragment, EventObserver { toStar ->
                val pref = PreferenceManager.getDefaultSharedPreferences(context)
                val showSnackbar = pref?.getBoolean("starring_show_snackbar", true) ?: true
                if (!showSnackbar) return@EventObserver
                val msgId = if (toStar) R.string.starred else R.string.unstarred
                Snackbar.make(cibBookmark, msgId, Snackbar.LENGTH_SHORT).run {
                    setAction(R.string.starred_unstarred_not_show) {
                        pref?.edit { putBoolean("starring_show_snackbar", false) }
                    }
                    show()
                }
            })

            authEvent.observe(this@SessionInfoFragment, EventObserver { command ->
                when (command) {
                    Authentication.LOGIN_REQUEST -> requestLogin()
                    Authentication.LOGIN_CONFIRMED -> logIn()
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
                    cibBookmark, getString(R.string.login_error, response?.error?.errorCode ?: 0),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}


