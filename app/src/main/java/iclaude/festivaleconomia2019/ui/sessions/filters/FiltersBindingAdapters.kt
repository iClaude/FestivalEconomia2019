package iclaude.festivaleconomia2019.ui.sessions.filters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.ItemFilterChipBinding
import iclaude.festivaleconomia2019.model.data_classes.Tag
import iclaude.festivaleconomia2019.model.data_classes.Tag.Companion.CATEGORY_TOPIC
import iclaude.festivaleconomia2019.model.data_classes.Tag.Companion.CATEGORY_TYPE
import iclaude.festivaleconomia2019.model.data_classes.colorInt
import iclaude.festivaleconomia2019.model.data_classes.fontColorInt
import iclaude.festivaleconomia2019.ui.sessions.SessionListViewModel

// Filter sheet binding adapters.
// Title. If filter is not set, display "Filters", otherwise display "x sessions" (number of filtered sessions).
@BindingAdapter("app:isFilterSet", "app:sessionsFiltered", requireAll = true)
fun title(textView: TextView, isFilterSet: Boolean, sessions: Int) {
    var str: String = textView.context.resources.getString(R.string.filters)

    if (isFilterSet) {
        str = textView.context.resources.getQuantityString(R.plurals.filtered_sessions, sessions, sessions)
    }
    textView.text = str
}

@BindingAdapter("app:filterStarred")
fun checkWithStarredFilter(chip: Chip, filter: Filter) {
    chip.isChecked = filter.isStarred()
}

@BindingAdapter("app:tags", "app:viewModel", requireAll = true)
fun addTags(chipGroup: ChipGroup, tags: List<Tag>, viewModel: SessionListViewModel) {
    chipGroup.removeAllViews()
    val cat = when (chipGroup.id) {
        R.id.cgTopics -> CATEGORY_TOPIC
        else -> CATEGORY_TYPE
    }

    for (tag in tags) {
        if (tag.category != cat) continue
        val context = chipGroup.context
        val binding = ItemFilterChipBinding.inflate(LayoutInflater.from(context), chipGroup, false).apply {
            setTag(tag)
            filter = viewModel.filterSelected.value
            setViewModel(viewModel)
        }
        chipGroup.addView(binding.root)
    }
}

@BindingAdapter("app:uncheckTags")
fun uncheckTags(chipGroup: ChipGroup, count: Int) {
    if (count > 0) chipGroup.clearCheck()
}

@BindingAdapter("app:uncheckTag")
fun uncheckTag(chip: Chip, count: Int) {
    if (count > 0) chip.isChecked = false
}

@BindingAdapter("app:showFiltersSet")
fun showFiltersSet(constraintLayout: ConstraintLayout, hasAnyFilters: Boolean) {
    BottomSheetBehavior.from(constraintLayout).apply {
        isHideable = !hasAnyFilters
        skipCollapsed = !hasAnyFilters
    }

}

@BindingAdapter("app:goneUnless")
fun goneUnless(view: View, isToShow: Boolean) {
    view.visibility = if (isToShow) View.VISIBLE else View.GONE
}

// Filter chip binding adapters.
@BindingAdapter("app:colors")
fun colorChip(chip: Chip, tag: Tag) {
    val colorStateList = createChipColorStateList(tag.colorInt)
    val fontColorStateList = createChipColorStateList(tag.fontColorInt)
    chip.apply {
        chipBackgroundColor = colorStateList
        setTextColor(fontColorStateList)
    }
}

@BindingAdapter("app:listeners", "app:tag", requireAll = true)
fun addChipListeners(chip: Chip, viewModel: SessionListViewModel, tag: Tag) {
    chip.run {
        setOnCheckedChangeListener { buttonView, isChecked ->
            updateFilter(isChecked, viewModel, buttonView, tag)
        }
    }
}

// Utility functions.
private fun updateFilter(toAdd: Boolean, viewModel: SessionListViewModel, view: View, tag: Tag) {
    val filter = viewModel.filterSelected.value
    when (toAdd) {
        true -> filter?.tags?.add(tag)
        else -> filter?.tags?.remove(tag)
    }
    viewModel.updateFilter(filter)
}

private fun createChipColorStateList(color: Int): ColorStateList {
    val states = arrayOf(
        intArrayOf(android.R.attr.state_checked), // checked
        intArrayOf(-android.R.attr.state_checked) // unchecked
    )

    val colors = intArrayOf(color, color)

    return ColorStateList(states, colors)
}


