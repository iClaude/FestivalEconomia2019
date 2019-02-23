package iclaude.festivaleconomia2019.ui.sessions.filters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
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
import iclaude.festivaleconomia2019.ui.sessions.TagAdapter

// Filter sheet binding adapters.

// Change header alpha on view model when sliding the bottom sheet.
@BindingAdapter("app:bottomSheetCallback")
fun addBottomSheetCallback(view: View, viewModel: SessionListViewModel) {
    with(BottomSheetBehavior.from(view)) {
        setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                viewModel.titleHeaderAlphaObs.set(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        })
    }
}

// Filter sheet is hideable only when filter is not set. Otherwise it gets collapsed when dismissed, displaying the tags selected.
@BindingAdapter("app:filterSheetHideable")
fun filterSheetHideable(constraintLayout: ConstraintLayout, hasAnyFilters: Boolean) {
    BottomSheetBehavior.from(constraintLayout).apply {
        isHideable = !hasAnyFilters
        skipCollapsed = !hasAnyFilters
    }
}

// Title. If filter is not set, display "Filters", otherwise display "x sessions" (number of filtered sessions).
@BindingAdapter("app:isFilterSet", "app:sessionsFiltered", requireAll = true)
fun title(textView: TextView, isFilterSet: Boolean, sessions: Int) {
    var str: String = textView.context.resources.getString(R.string.filters)

    if (isFilterSet) {
        str = textView.context.resources.getQuantityString(R.plurals.filtered_sessions, sessions, sessions)
    }
    textView.text = str
}

// Topics and types ChipGroups. Add all tags.
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

// ChipGroups: when filter is cleared, uncheck all tags.
@BindingAdapter("app:uncheckTags")
fun uncheckTags(chipGroup: ChipGroup, count: Int) {
    if (count > 0) chipGroup.clearCheck()
}

@BindingAdapter("app:goneUnless")
fun goneUnless(view: View, isToShow: Boolean) {
    view.visibility = if (isToShow) View.VISIBLE else View.GONE
}

@BindingAdapter("app:filterTags")
fun filterTags(recyclerView: RecyclerView, filterTags: List<Tag>?) {
    recyclerView.adapter = (recyclerView.adapter as? TagAdapter ?: TagAdapter())
        .apply {
            tags = filterTags ?: emptyList()
            notifyDataSetChanged()
        }
}

// Add a scrolling listener to NestedScrollView to update scrollY property in view model.
@BindingAdapter("app:scrollListener")
fun addScrollListener(nestedScrollView: NestedScrollView, viewModel: SessionListViewModel) {
    nestedScrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
        viewModel.scrollYObs.set(scrollY)
    }
}

// Add elevation to header when scrolling.
@BindingAdapter("app:scrolling")
fun changeElevationWhenScrolling(view: View, scrollY: Int) {
    view.isActivated = when (scrollY) {
        in 1..Int.MAX_VALUE -> true
        else -> false
    }
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
            updateFilter(isChecked, viewModel, tag)
        }
    }
}

// Utility functions.
private fun updateFilter(toAdd: Boolean, viewModel: SessionListViewModel, tag: Tag) {
    val filter = viewModel.filterSelected.value ?: Filter()
    when(tag.category) {
        CATEGORY_TYPE -> filter.tagsTypes
        else -> filter.tagsTopics
    }.let {
        when (toAdd) {
            true -> it.add(tag)
            else -> it.remove(tag)
        }
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


