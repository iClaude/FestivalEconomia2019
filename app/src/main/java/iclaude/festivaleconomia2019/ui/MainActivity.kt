package iclaude.festivaleconomia2019.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.Session

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun testFirestore(v: View) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("events").document("festival_economia_2019").collection("sessions").document("02")

        val myTextView = findViewById<TextView>(R.id.textView)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val event = document.toObject(Session::class.java)
                    myTextView.text = event.toString()
                } else {
                    myTextView.text = "no document found"
                }
            }
            .addOnFailureListener { exception ->
                myTextView.text = "exception occured"
            }
    }
}
