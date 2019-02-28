package iclaude.festivaleconomia2019.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
                Log.w(TAG, "Error adding new user to Firebase", it)
            }
    }

    fun getStarredSessions(): List<String> {
        var starredSessions: List<String> = mutableListOf()

        val user = FirebaseAuth.getInstance().currentUser ?: return starredSessions

        val db = FirebaseFirestore.getInstance()
        db.collection(FIREBASE_PATH_USERS).document(user.uid).get()
            .addOnSuccessListener {
                val userInFirebase = it.toObject(User::class.java)
                userInFirebase?.let { userInFirebase ->
                    eventDataLive.value?.sessions?.let { sessions ->
                        for (sessionId in userInFirebase.starredSessions) {
                            sessions[sessionId.toInt()].starred = true
                        }
                    }
                    starredSessions = userInFirebase.starredSessions
                }
            }
            .addOnFailureListener {
                Log.w(TAG, "Error in searching user in Firebase", it)

            }

        return starredSessions
    }

    fun starOrUnstarSession(sessionId: String, toStar: Boolean) {
        // update Firebase db
        val user = FirebaseAuth.getInstance().currentUser ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection(FIREBASE_PATH_USERS).document(user.uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val userInFirebase = it.toObject(User::class.java)
                    userInFirebase?.let { userInFirebase ->
                        val starredSessions = userInFirebase.starredSessions.toMutableList()
                        if (toStar) {
                            if (!starredSessions.contains(sessionId)) starredSessions.add(sessionId)
                        } else {
                            starredSessions.remove(sessionId)
                        }
                        updateStarredSessions(db, "$FIREBASE_PATH_USERS${user.uid}", starredSessions)
                    }
                }
            }
            .addOnFailureListener {
                Log.w(TAG, "Star/Unstar session - Error in searching User in Firebase", it)
            }

        // update local repository
        eventDataLive.value?.sessions?.let { sessions ->
            sessions[sessionId.toInt()].starred = toStar
        }
    }

    private fun updateStarredSessions(db: FirebaseFirestore, path: String, sessions: List<String>) {
        db.document(path)
            .update("starredSessions", sessions)
            .addOnSuccessListener { Log.d(TAG, "starredSessions updated") }
            .addOnFailureListener { Log.w(TAG, "Error updating starredSessions", it) }
    }

    fun canceLoadingData() {
        job.cancel()
    }

    companion object {
        private const val FIREBASE_PATH_USERS = "/events/festival_economia_2019/users/"
        private const val FIREBASE_PATH_SESSIONS = "/events/festival_economia_2019/sessions/"
    }
}