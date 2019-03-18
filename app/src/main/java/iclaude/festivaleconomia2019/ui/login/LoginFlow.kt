package iclaude.festivaleconomia2019.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import iclaude.festivaleconomia2019.ui.utils.Event

interface LoginFlow {
    enum class Authentication { LOGIN_REQUEST, LOGIN_CONFIRMED, LOGOUT_REQUEST, LOGOUT_CONFIRMED }

    val _authEvent: MutableLiveData<Event<Authentication>>
    val authEvent: LiveData<Event<Authentication>>
        get() = _authEvent

    fun startAuthFlow() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            _authEvent.value = Event(Authentication.LOGIN_REQUEST)
        } else {
            _authEvent.value = Event(Authentication.LOGOUT_REQUEST)
        }
    }

    fun confirmLogin() {
        _authEvent.value = Event(Authentication.LOGIN_CONFIRMED)
    }

    fun confirmLogout() {
        _authEvent.value = Event(Authentication.LOGOUT_CONFIRMED)
    }

    fun getLoginProviders() = arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.FacebookBuilder().build(),
        AuthUI.IdpConfig.TwitterBuilder().build()
    )

    fun onUserLoggedIn(user: FirebaseUser)

    fun onUserLoggedOut()

    fun addUserToFirebase(user: FirebaseUser)
}