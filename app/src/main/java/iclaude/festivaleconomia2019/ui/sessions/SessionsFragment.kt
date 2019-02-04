package iclaude.festivaleconomia2019.ui.sessions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.ItemSessionBinding
import iclaude.festivaleconomia2019.databinding.ItemSessionTagBinding
import iclaude.festivaleconomia2019.model.data_classes.Tag
import kotlinx.android.synthetic.main.fragment_sessions.*


private const val ARG_TIME = "time"


class SessionsFragment : Fragment() {
    private val TAG = "VIEW_MODEL"
    private var param1: Long? = null
    private lateinit var mViewModel: SessionsViewModel
    private val mRvAdapter = SessionListAdapter()

    companion object {
        @JvmStatic
        fun newInstance(param1: Long) =
            SessionsFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TIME, param1)
                }
            }
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(rvSessions) {
            adapter = mRvAdapter
            setHasFixedSize(true)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModel = ViewModelProviders.of(activity!!).get(SessionsViewModel::class.java)
        mViewModel.sessionsInfoLive.observe(this, Observer {
            mRvAdapter.submitList(it)
        })
    }


    // Main RecyclerView classes.
    inner class SessionDiffCallback : DiffUtil.ItemCallback<SessionsDisplayInfo>() {
        override fun areItemsTheSame(oldItem: SessionsDisplayInfo, newItem: SessionsDisplayInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SessionsDisplayInfo, newItem: SessionsDisplayInfo): Boolean {
            return oldItem.title == newItem.title && oldItem.liveStreamed == newItem.liveStreamed && oldItem.startTimestamp == newItem.startTimestamp && oldItem.endTimestamp == newItem.endTimestamp
        }
    }

    inner class SessionViewHolder(private val binding: ItemSessionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sessionInfo: SessionsDisplayInfo) {
            with(binding) {
                this.sessionInfo = sessionInfo
                executePendingBindings()
            }
        }
    }

    inner class SessionListAdapter() :
        ListAdapter<SessionsDisplayInfo, SessionViewHolder>(SessionDiffCallback()) {

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
}

// Classes for tags RecyclerView.
class TagAdapter : RecyclerView.Adapter<TagViewHolder>() {
    private val TAG = "TAGS"

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
