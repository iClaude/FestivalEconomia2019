package iclaude.festivaleconomia2019.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.JSONparser.JSONparser
import iclaude.festivaleconomia2019.model.data_classes.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.InputStream


class EventDataRepository(private val inputStream: InputStream) {
    private val TAG by lazy { this.javaClass.simpleName }
    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    private val _eventDataLive: MutableLiveData<EventData> = MutableLiveData()
    val eventDataLive: LiveData<EventData>
        get() = _eventDataLive

    fun loadEventDataFromJSONFile() {
        ioScope.launch {
            _eventDataLive.postValue(JSONparser.parseEventData(inputStream))
            updateWithStarredSessionsFromFirebase()
        }
    }

    fun updateWithStarredSessionsFromFirebase() {
        val user = FirebaseAuth.getInstance().currentUser ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection(FIREBASE_PATH_USERS).document(user.uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val userInFirebase = it.toObject(User::class.java)
                    userInFirebase?.let { userInFirebase ->
                        val eventData = eventDataLive.value
                        eventData?.sessions?.let { sessions ->
                            for (sessionId in userInFirebase.starredSessions) {
                                sessions[sessionId.toInt()].starred = true
                            }
                            _eventDataLive.postValue(eventData)
                        }
                    }
                } else {
                    db.collection(FIREBASE_PATH_USERS).document(user.uid)
                        .set(User(uid = user.uid))
                        .addOnSuccessListener { Log.d(TAG, "New User successfully added!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error adding new User", e) }
                }
            }
            .addOnFailureListener {
                Log.w(TAG, "Error in searching User in Firebase", it)

            }
    }

    fun canceLoadingData() {
        job.cancel()
    }

    companion object {
        private const val FIREBASE_PATH_USERS = "/events/festival_economia_2019/users/"
        private const val FIREBASE_PATH_SESSIONS = "/events/festival_economia_2019/sessions/"
    }
}