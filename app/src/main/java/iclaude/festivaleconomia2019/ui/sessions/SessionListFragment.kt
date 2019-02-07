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
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime


private const val ARG_YEAR = "year"
private const val ARG_MONTH = "month"
private const val ARG_DAY = "day"
private const val ARG_HOUR = "hour"
private const val ARG_MINUTE = "minute"
private const val ARG_SECOND = "second"
private const val ARG_ZONE_ID = "zoneId"


class SessionListFragment : Fragment() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(SessionListViewModel::class.java) }
    private val rvAdapter = SessionListAdapter()
    private lateinit var daySelected: ZonedDateTime

    companion object {
        @JvmStatic
        fun newInstance(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int, zoneId: String) =
            SessionListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_YEAR, year)
                    putInt(ARG_MONTH, month)
                    putInt(ARG_DAY, day)
                    putInt(ARG_HOUR, hour)
                    putInt(ARG_MINUTE, minute)
                    putInt(ARG_SECOND, second)
                    putString(ARG_ZONE_ID, zoneId)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val year = it.getInt(ARG_YEAR)
            val month = it.getInt(ARG_MONTH)
            val day = it.getInt(ARG_DAY)
            val hour = it.getInt(ARG_HOUR)
            val minute = it.getInt(ARG_MINUTE)
            val second = it.getInt(ARG_SECOND)
            val zoneId = it.getString(ARG_ZONE_ID)
            daySelected = ZonedDateTime.of(year, month, day, hour, minute, second, 0, ZoneId.of(zoneId))
        }

        viewModel.daySelected.value = daySelected
        viewModel.sessionsInfoFilteredLive.observe(this, Observer {
            rvAdapter.submitList(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_sessions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(rvSessions) {
            adapter = rvAdapter
            setHasFixedSize(true)
        }
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

    inner class SessionListAdapter :
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
