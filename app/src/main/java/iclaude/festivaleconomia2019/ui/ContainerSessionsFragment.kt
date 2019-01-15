package iclaude.festivaleconomia2019.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.JSONparser.JSONparser


class ContainerSessionsFragment : Fragment() {

    private lateinit var textView: TextView
    private lateinit var button: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_container_sessions, container, false)
        textView = root.findViewById<TextView>(R.id.textView2)
        button = root.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val inputStream = context?.assets?.open("event_data_2019.json").let {
                val eventData = JSONparser.parseEventData(it!!)
                textView.text = eventData.toString()
            }
        }

        return root
    }


}
