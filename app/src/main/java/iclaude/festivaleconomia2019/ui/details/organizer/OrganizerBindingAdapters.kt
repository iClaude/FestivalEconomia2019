package iclaude.festivaleconomia2019.ui.details.organizer

import androidx.databinding.BindingAdapter
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.absoluteValue

/**
 * App bar.
 */

// Compute percentage of collapsed app bar.
@BindingAdapter("app:onOffsetChangedListener")
fun addOnOffsetChangedListener(appBarLayout: AppBarLayout, viewModel: OrganizerViewModel) {
    appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        val percCollapsed = verticalOffset.absoluteValue.toFloat() / appBarLayout.totalScrollRange
        viewModel.appBarCollapsedPercentageObs.set(percCollapsed)
    })
}