package iclaude.festivaleconomia2019.ui.map

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import iclaude.festivaleconomia2019.databinding.ItemSessionMapBinding
import iclaude.festivaleconomia2019.model.data_classes.Session
import iclaude.festivaleconomia2019.utils.layoutInflater

/**
 * RecyclerView classes for the list of sessions held at a particular location,
 * displayed in the BottomSheet.
 */

class SessionViewHolder(private val binding: ItemSessionMapBinding, private val mapViewModel: MapViewModel) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(session: Session) {
        with(binding) {
            this.session = session
            this.viewModel = mapViewModel
            executePendingBindings()
        }
    }
}

class SessionListAdapter(private val mapViewModel: MapViewModel) :
    ListAdapter<Session, SessionViewHolder>(SESSION_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val layoutInflater = parent.context.layoutInflater
        val binding = ItemSessionMapBinding.inflate(layoutInflater, parent, false)
        return SessionViewHolder(binding, mapViewModel)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = getItem(position)
        holder.bind(session)
    }

    // Implementation of DiffUtil.ItemCallback.
    companion object {
        private val SESSION_COMPARATOR = object : DiffUtil.ItemCallback<Session>() {
            override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
                // These are the relevant properties that cause visual differences between items.
                return oldItem.title == newItem.title
                        && oldItem.youtubeUrl == newItem.youtubeUrl
                        && oldItem.startTimestamp == newItem.startTimestamp
                        && oldItem.endTimestamp == newItem.endTimestamp
                        && oldItem.starred == newItem.starred

            }
        }
    }
}
