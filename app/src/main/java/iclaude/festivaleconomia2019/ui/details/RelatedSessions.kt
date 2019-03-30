package iclaude.festivaleconomia2019.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import iclaude.festivaleconomia2019.ui.utils.Event

/**
 * Both SessionInfoFragment and OrganizerFragment have a list of related sessions.
 * This interface defines the common functionality.
 */
interface RelatedSessions {
    /**
     * User clicks on a session to see more details.
     */
    val _goToSessionEvent: MutableLiveData<Event<String>>
    val goToSessionEvent: LiveData<Event<String>>
        get() = _goToSessionEvent

    fun goToSession(sessionId: String) {
        _goToSessionEvent.value = Event(sessionId)
    }

    /**
     * User stars/unstars a related session.
     */

    /* If the user logs in-out or stars/unstars sessions in this Fragment, SessionListFragment
       should also be updated (showing avatar and updating starred sessions).*/
    val _loginOperationsEvent: MutableLiveData<Event<Any>>
    val loginOperationsEvent: LiveData<Event<Any>>
        get() = _loginOperationsEvent

    val _showSnackBarForStarringEvent: MutableLiveData<Event<Boolean>>
    val showSnackBarForStarringEvent: LiveData<Event<Boolean>>
        get() = _showSnackBarForStarringEvent

    fun starOrUnstarSession(sessionId: String, toStar: Boolean) {
        _showSnackBarForStarringEvent.value = Event(toStar)
        _loginOperationsEvent.value = Event(Unit)
    }
}