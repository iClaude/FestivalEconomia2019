package iclaude.festivaleconomia2019.ui.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import iclaude.festivaleconomia2019.databinding.ItemSessionMapBinding
import iclaude.festivaleconomia2019.model.data_classes.Session

// RecyclerView classe for the list of sessions held at a particular location, displayed in the BottomSheet.

class SessionDiffCallback : DiffUtil.ItemCallback<Session>() {
    override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem.title == newItem.title
                && oldItem.youtubeUrl == newItem.youtubeUrl
                && oldItem.startTimestamp == newItem.startTimestamp
                && oldItem.startTimestamp == newItem.startTimestamp

    }
}

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
    ListAdapter<Session, SessionViewHolder>(SessionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSessionMapBinding.inflate(layoutInflater, parent, false)
        return SessionViewHolder(binding, mapViewModel)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = getItem(position)
        holder.bind(session)
    }
}
