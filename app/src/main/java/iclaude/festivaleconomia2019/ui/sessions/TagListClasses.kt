package iclaude.festivaleconomia2019.ui.sessions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import iclaude.festivaleconomia2019.databinding.ItemSessionTagBinding
import iclaude.festivaleconomia2019.model.data_classes.Tag

/**
 * RecyclerView classes for the list of tags associated with each session.
 */
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