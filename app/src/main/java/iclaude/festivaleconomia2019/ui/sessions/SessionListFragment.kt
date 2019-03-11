package iclaude.festivaleconomia2019.ui.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import iclaude.festivaleconomia2019.databinding.FragmentSessionListBinding
import iclaude.festivaleconomia2019.databinding.ItemSessionBinding
import iclaude.festivaleconomia2019.databinding.ItemSessionTagBinding
import iclaude.festivaleconomia2019.model.data_classes.Tag

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
        rvAdapter = SessionListAdapter()
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

    // Main RecyclerView classes.
    inner class SessionDiffCallback : DiffUtil.ItemCallback<SessionInfoForList>() {
        override fun areItemsTheSame(oldItem: SessionInfoForList, newItem: SessionInfoForList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SessionInfoForList, newItem: SessionInfoForList): Boolean {
            return oldItem.title == newItem.title
                    && oldItem.liveStreamed == newItem.liveStreamed
                    && oldItem.startTimestamp == newItem.startTimestamp
                    && oldItem.endTimestamp == newItem.endTimestamp
                    && oldItem.lenLoc == newItem.lenLoc
                    && oldItem.tags == newItem.tags
                    && oldItem.starred == newItem.starred
        }
    }

    inner class SessionViewHolder(private val binding: ItemSessionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sessionInfo: SessionInfoForList) {
            with(binding) {
                this.sessionInfo = sessionInfo
                this.viewModel = this@SessionListFragment.viewModel
                executePendingBindings()
            }
        }
    }

    inner class SessionListAdapter :
        ListAdapter<SessionInfoForList, SessionViewHolder>(SessionDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemSessionBinding.inflate(layoutInflater, parent, false)
            return SessionViewHolder(binding)
        }

        override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
            val sessionInfo = getItem(position)
            holder.bind(sessionInfo)
        }
    }

    interface ListEvents {
        fun onStarClicked(sessionId: String)
    }
}

// Classes for tags RecyclerView.
class TagAdapter : RecyclerView.Adapter<TagViewHolder>() {
    var tags = emptyList<Tag>()

    override fun getItemCount() = tags.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        return TagViewHolder(
            ItemSessionTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(tags[position])
    }
}

class TagViewHolder(private val binding: ItemSessionTagBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(tag: Tag) {
        binding.tag = tag
        binding.executePendingBindings()
    }
}
