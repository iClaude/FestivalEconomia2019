package iclaude.festivaleconomia2019.ui.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import iclaude.festivaleconomia2019.R
import kotlinx.android.synthetic.main.fragment_sessions.*


private const val ARG_TIME = "time"


class SessionsFragment : Fragment() {
    private val TAG by lazy { this.javaClass.simpleName }
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

        return root
    }

    override fun onResume() {
        super.onResume()

        tvParam.text = param1.toString()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Long) =
            SessionsFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TIME, param1)
                }
            }
    }
}
