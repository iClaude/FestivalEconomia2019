package iclaude.festivaleconomia2019.model

import android.os.Handler
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

    private val handler = Handler()
    private var listenerRemovePending = false

    override fun onActive() {
        super.onActive()

        if (listenerRemovePending)
            handler.removeCallbacks(removeListenerRunnable)
        else
            registration = collRef.addSnapshotListener(mListener)

        listenerRemovePending = false
    }

    override fun onInactive() {
        super.onInactive()
        handler.postDelayed(removeListenerRunnable, 2000)
        listenerRemovePending = true
    }


    private val mListener = EventListener<QuerySnapshot> { snapshot, e ->
        if (e != null) {
            Log.w(TAG, "Listen failed", e)
            return@EventListener
        }

        value = snapshot
    }

    private val removeListenerRunnable = object : Runnable {
        override fun run() {
            registration.remove()
            listenerRemovePending = false
        }
    }

}