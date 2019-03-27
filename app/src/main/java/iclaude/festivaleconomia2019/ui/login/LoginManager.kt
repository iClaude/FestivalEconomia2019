package iclaude.festivaleconomia2019.ui.login

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.sessions.RC_SIGN_IN

object LoginManager {

    // Request user confirmation for login.
    fun requestLogin(context: Context, loginFlow: LoginFlow) {
        val posButtonListener = DialogInterface.OnClickListener { dialog, which ->
            loginFlow.confirmLogin()
            dialog?.dismiss()
        }
        val negButtonListener = DialogInterface.OnClickListener { dialog, which -> dialog?.dismiss() }
        showAlertDialog(
            R.string.login_confirm_title, R.string.login_confirm_msg, R.string.login_confirm_accept,
            R.string.login_confirm_cancel, posButtonListener, negButtonListener,
            context
        )
    }

    // User has confirmed login.
    fun logIn(fragment: Fragment, loginFlow: LoginFlow) {
        // Choose authentication providers.
        val providers = loginFlow.getLoginProviders()

        // Create and launch sign-in intent.
        fragment.startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.AppTheme)
                .setLogo(R.mipmap.ic_launcher_round)
                .setTosAndPrivacyPolicyUrls(
                    "https://drive.google.com/file/d/19iOz3EP86Z0K2i3nsbiwjQQuABxzktC6/view?usp=sharing",
                    "https://drive.google.com/file/d/19iOz3EP86Z0K2i3nsbiwjQQuABxzktC6/view?usp=sharing"
                )
                .build(),
            RC_SIGN_IN
        )
    }

    // Request user confirmation for logout.
    fun requestLogout(context: Context, loginFlow: LoginFlow) {
        val posButtonListener = DialogInterface.OnClickListener { dialog, which ->
            loginFlow.confirmLogout()
            dialog?.dismiss()
        }
        val negButtonListener = DialogInterface.OnClickListener { dialog, which -> dialog?.dismiss() }
        showAlertDialog(
            R.string.logout_dialog_title, R.string.logout_dialog_msg, R.string.logout_dialog_accept,
            R.string.logout_dialog_cancel, posButtonListener, negButtonListener,
            context
        )
    }

    // User has confirmed logout.
    fun logOut(context: Context, loginFlow: LoginFlow) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                loginFlow.onUserLoggedOut()
            }
    }

    fun loginResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        viewForSnackBar: View,
        context: Context,
        loginFlow: LoginFlow
    ) {
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {
                    loginFlow.onUserLoggedIn(it)
                }
            } else {
                Snackbar.make(
                    viewForSnackBar, context.getString(R.string.login_error, response?.error?.errorCode ?: 0),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun showAlertDialog(
        @StringRes title: Int, @StringRes msg: Int, @StringRes posButton: Int,
        @StringRes negButton: Int, posButtonListener: DialogInterface.OnClickListener,
        negButtonListener: DialogInterface.OnClickListener,
        context: Context
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(posButton, posButtonListener)
            .setNegativeButton(negButton, negButtonListener)
            .show()
    }
}