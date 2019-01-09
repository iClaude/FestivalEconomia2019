package iclaude.festivaleconomia2019.model

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

/*
    Livedata connected to a Firestore collection reference to obtain realtime updates.
 */
class FirestoreCollectionLiveData(private val collRef: CollectionReference) : LiveData<QuerySnapshot>() {

    private val TAG = this.javaClass.simpleName
    private lateinit var registration: ListenerRegistration

    override fun onActive() {
        super.onActive()
        registration = collRef.addSnapshotListener(mListener)
    }

    override fun onInactive() {
        super.onInactive()
        registration.remove()
    }


    private val mListener = EventListener<QuerySnapshot> { snapshot, e ->
        if (e != null) {
            Log.w(TAG, "Listen failed", e)
            return@EventListener
        }

        value = snapshot
    }
}