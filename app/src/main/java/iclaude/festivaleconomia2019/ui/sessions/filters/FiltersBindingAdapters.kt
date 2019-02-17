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
import iclaude.festivaleconomia2019.ui.sessions.SessionListViewModel

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

@BindingAdapter("app:tags", "app:viewModel", requireAll = true)
fun addTags(chipGroup: ChipGroup, tags: List<Tag>, viewModel: SessionListViewModel) {
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
                setTag(tag)
                isChecked = viewModel.filterSelected.value?.tags?.contains(tag) ?: false
                isCloseIconVisible = isChecked

                setOnCloseIconClickListener {
                    isChecked = false
                }

                setOnCheckedChangeListener { buttonView, isChecked ->
                    isCloseIconVisible = isChecked
                    updateFilter(isChecked, viewModel, buttonView)
                }

                chipGroup.addView(this)
            }
        }
    }
}

fun updateFilter(toAdd: Boolean, viewModel: SessionListViewModel, view: View) {
    val filter = viewModel.filterSelected.value
    val tag = view.tag as Tag
    when (toAdd) {
        true -> filter?.tags?.add(tag)
        else -> filter?.tags?.remove(tag)
    }
    viewModel.filterSelected.value = filter
}


