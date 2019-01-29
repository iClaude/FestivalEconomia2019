package iclaude.festivaleconomia2019.ui.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import iclaude.festivaleconomia2019.R


private const val ARG_TIME = "time"


class SessionFragment : Fragment() {
    private var param1: Long? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getLong(ARG_TIME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_sessions, container, false)
        root.findViewById<TextView>(R.id.tvParam).text = param1.toString()

        return root
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: Long) =
            SessionFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TIME, param1)
                }
            }
    }
}
