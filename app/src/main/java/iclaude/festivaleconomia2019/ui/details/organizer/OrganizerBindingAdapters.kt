package iclaude.festivaleconomia2019.ui.details.organizer

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.plusAssign
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.ItemSessionRelatedBinding
import iclaude.festivaleconomia2019.model.data_classes.Location
import iclaude.festivaleconomia2019.model.data_classes.Session
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

/**
 * Loads a [Speaker]'s photo or picks a default avatar if no photo is specified.
 */
@BindingAdapter("app:speakerImage")
fun speakerImage(imageView: ImageView, organizerInfo: OrganizerInfo?) {
    organizerInfo ?: return

    // Want a 'random' default avatar but should be stable as used on both session details &
    // speaker detail screens (as a shared element transition), so use first initial to pick.
    val placeholderId = when (organizerInfo.name[0].toLowerCase()) {
        in 'a'..'i' -> R.drawable.ic_default_avatar_1
        in 'j'..'r' -> R.drawable.ic_default_avatar_2
        else -> R.drawable.ic_default_avatar_3
    }

    if (organizerInfo.thumbnailUrl.isNullOrBlank()) {
        imageView.setImageResource(placeholderId)
    } else {
        val imageLoad = Glide.with(imageView)
            .load(organizerInfo.thumbnailUrl)
            .apply(
                RequestOptions()
                    .placeholder(placeholderId)
                    .circleCrop()
            )

        imageLoad.into(imageView)
    }
}

// List of sessions held by this organizer.
@BindingAdapter("app:organizerSessions", "app:viewModel", requireAll = true)
fun addRelatedSessions(layout: LinearLayout, relatedSessions: List<Session>, viewModel: OrganizerViewModel) {
    val eventData = viewModel.repository.eventDataLive.value ?: return

    val context = layout.context
    val locations: List<Location> = eventData.locations

    for (session in relatedSessions) {
        val location = locations[session.location.toInt()]
        val binding = ItemSessionRelatedBinding.inflate(LayoutInflater.from(context), layout, false).apply {
            setSession(session)
            setLocation(location)
            loginFlow = viewModel
            setRelatedSessions(viewModel)
        }

        val view = binding.root.apply { tag = session.id }
        layout += view
    }
}
