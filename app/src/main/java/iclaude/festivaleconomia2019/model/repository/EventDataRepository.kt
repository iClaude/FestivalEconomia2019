package iclaude.festivaleconomia2019.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import iclaude.festivaleconomia2019.model.JSONparser.EventData
import iclaude.festivaleconomia2019.model.JSONparser.JSONparser
import iclaude.festivaleconomia2019.model.data_classes.User
import iclaude.festivaleconomia2019.utils.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.InputStream


class EventDataRepository(private val inputStream: InputStream) {

    //************************** Local JSON file **************************

    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    private val _eventDataLive: MutableLiveData<EventData> = MutableLiveData()
    val eventDataLive: LiveData<EventData>
        get() = _eventDataLive

    fun loadEventDataFromJSONFile() {
        ioScope.launch {
            _eventDataLive.postValue(JSONparser.parseEventData(inputStream))
        }
    }

    //*************************** Firebase **********************************

    fun addUser(user: FirebaseUser) {
        val db = FirebaseFirestore.getInstance()
        db.collection(FIREBASE_PATH_USERS).document(user.uid).get()
            .addOnSuccessListener {
                if (!it.exists()) {
                    db.collection(FIREBASE_PATH_USERS).document(user.uid)
                        .set(User(uid = user.uid))
                        .addOnSuccessListener { Log.d(TAG, "New user successfully added") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error adding new user", e) }
                }
            }
            .addOnFailureListener {
                Log.w(TAG, "Error getting user in Firebase", it)
            }
    }

    fun getStarredSessions(successCallback: OnSuccessListener<DocumentSnapshot>) {
        val user = FirebaseAuth.getInstance().currentUser ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection(FIREBASE_PATH_USERS).document(user.uid).get()
            .addOnSuccessListener(successCallback)
            .addOnFailureListener {
                Log.w(TAG, "Error getting user in Firebase", it)

            }
    }

    fun starOrUnstarSession(sessionId: String, toStar: Boolean) {
        val user = FirebaseAuth.getInstance().currentUser ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection(FIREBASE_PATH_USERS).document(user.uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val userInFirebase = it.toObject(User::class.java)
                    userInFirebase?.let { userInFirebase ->
                        val starredSessionsUpdated = userInFirebase.starredSessions.toMutableList()
                        if (toStar) {
                            if (!starredSessionsUpdated.contains(sessionId)) starredSessionsUpdated.add(sessionId)
                        } else {
                            starredSessionsUpdated.remove(sessionId)
                        }
                        updateStarredSessions(db, "$FIREBASE_PATH_USERS${user.uid}", starredSessionsUpdated)
                    }
                }
            }
            .addOnFailureListener {
                Log.w(TAG, "Error getting user in Firebase", it)
            }
    }

    private fun updateStarredSessions(db: FirebaseFirestore, path: String, sessions: List<String>) {
        db.document(path)
            .update("starredSessions", sessions)
            .addOnSuccessListener { Log.d(TAG, "User starred sessions updated") }
            .addOnFailureListener { Log.w(TAG, "Error updating user starred sessions", it) }
    }

    fun cancelLoadingData() {
        job.cancel()
    }

    companion object {
        private const val FIREBASE_PATH_USERS = "/events/festival_economia_2019/users/"
        private const val FIREBASE_PATH_SESSIONS = "/events/festival_economia_2019/sessions/"
    }
}