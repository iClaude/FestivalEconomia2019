package iclaude.festivaleconomia2019.model

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration

/*
    Livedata connected to a Firestore document reference to obtain realtime updates.
 */
class FirestoreDocumentLiveData(private val docRef: DocumentReference) : LiveData<DocumentSnapshot>() {

    private val TAG = this.javaClass.simpleName
    private lateinit var registration: ListenerRegistration

    override fun onActive() {
        super.onActive()
        registration = docRef.addSnapshotListener(mListener)
    }

    override fun onInactive() {
        super.onInactive()
        registration.remove()
    }


    private val mListener = EventListener<DocumentSnapshot> { snapshot, e ->
        if (e != null) {
            Log.w(TAG, "Listen failed", e)
            return@EventListener
        }

        value = snapshot
    }
}