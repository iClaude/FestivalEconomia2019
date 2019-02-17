package iclaude.festivaleconomia2019.ui.sessions.filters

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.data_classes.Tag
import iclaude.festivaleconomia2019.model.data_classes.Tag.Companion.CATEGORY_TOPIC
import iclaude.festivaleconomia2019.model.data_classes.Tag.Companion.CATEGORY_TYPE

@BindingAdapter("app:filterSet", "app:sessionsFiltered", requireAll = true)
fun fitleFilter(textView: TextView, filter: Filter, sessions: Int) {
    var str: String = textView.context.resources.getString(R.string.filters)

    if (filter.isFilterSet()) {
        str = textView.context.resources.getQuantityString(R.plurals.filtered_sessions, sessions, sessions)
    }
    textView.text = str
}

@BindingAdapter("app:showWithFilter")
fun showWithFilter(view: View, filter: Filter) {
    if (filter.isFilterSet())
        view.visibility = View.VISIBLE
    else
        view.visibility = View.INVISIBLE

}

@BindingAdapter("app:tags")
fun addTags(chipGroup: ChipGroup, tags: List<Tag>) {
    val cat = when (chipGroup.id) {
        R.id.cgTopics -> CATEGORY_TOPIC
        else -> CATEGORY_TYPE
    }

    for (tag in tags) {
        if (tag.category == cat) {
            val context = chipGroup.context
            val chip = LayoutInflater.from(context).inflate(R.layout.item_filter_chip, chipGroup, false) as Chip
            chip.run {
                text = tag.name
                isCloseIconVisible = true
                setOnCloseIconClickListener { chipGroup.removeView(this) }
                chipGroup.addView(this)
            }
        }
    }
}

