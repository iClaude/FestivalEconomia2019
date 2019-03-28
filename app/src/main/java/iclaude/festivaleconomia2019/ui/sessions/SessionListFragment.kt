package iclaude.festivaleconomia2019.ui.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import iclaude.festivaleconomia2019.databinding.FragmentSessionListBinding

class SessionListFragment : Fragment() {
    private lateinit var viewModel: SessionListViewModel
    private lateinit var binding: FragmentSessionListBinding
    private lateinit var rvAdapter: SessionListAdapter
    private var day: Int = 0

    companion object {
        private const val ARG_DAY = "day"

        @JvmStatic
        fun newInstance(time: Int) =
            SessionListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_DAY, time)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        day = arguments?.getInt(ARG_DAY) ?: 0

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(SessionListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        rvAdapter = SessionListAdapter(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionListBinding.inflate(inflater, container, false).apply {
            viewModel = this@SessionListFragment.viewModel
        }

        with(binding.rvSessions) {
            adapter = rvAdapter
            setHasFixedSize(true)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.run {
            sessionsInfoFilteredLive.observe(this@SessionListFragment, Observer { list ->
                initializeList(list.filter {
                    it.day == day
                })
            })
        }
    }


    private fun initializeList(sessions: List<SessionInfoForList>) {
        rvAdapter.apply {
            submitList(sessions)
            notifyDataSetChanged() // damn Google! why are you forcing me to do that???
        }

        val zoneId = getZoneId(context)
        binding.rvSessions.run {
            doOnNextLayout {
                // Recreate the decoration used for the sticky time headers
                clearDecorations()
                if (sessions.isNotEmpty()) {
                    addItemDecoration(
                        ScheduleTimeHeadersDecoration(
                            it.context, sessions, zoneId
                        )
                    )
                }
            }
        }
    }
}


