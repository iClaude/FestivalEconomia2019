package iclaude.festivaleconomia2019.ui.sessions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import iclaude.festivaleconomia2019.databinding.ItemSessionBinding

/**
 * RecyclerView classes for the main list of sessions.
 */

class SessionViewHolder(
    private val binding: ItemSessionBinding,
    private val sessionListViewModel: SessionListViewModel
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(sessionInfo: SessionInfoForList) {
        with(binding) {
            this.sessionInfo = sessionInfo
            this.viewModel = sessionListViewModel
            executePendingBindings()
        }
    }
}

class SessionListAdapter(private val sessionListViewModel: SessionListViewModel) :
    ListAdapter<SessionInfoForList, SessionViewHolder>(SESSION_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSessionBinding.inflate(layoutInflater, parent, false)
        return SessionViewHolder(binding, sessionListViewModel)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val sessionInfo = getItem(position)
        holder.bind(sessionInfo)
    }

    // Implementation of DiffUtil.ItemCallback.
    companion object {
        private val SESSION_COMPARATOR = object : DiffUtil.ItemCallback<SessionInfoForList>() {
            override fun areItemsTheSame(oldItem: SessionInfoForList, newItem: SessionInfoForList): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SessionInfoForList, newItem: SessionInfoForList): Boolean {
                // These parameters cause visual differences between items.
                return oldItem.title == newItem.title
                        && oldItem.liveStreamed == newItem.liveStreamed
                        && oldItem.startTimestamp == newItem.startTimestamp
                        && oldItem.endTimestamp == newItem.endTimestamp
                        && oldItem.lenLoc == newItem.lenLoc
                        && oldItem.tags == newItem.tags
                        && oldItem.starred == newItem.starred
            }
        }
    }
}