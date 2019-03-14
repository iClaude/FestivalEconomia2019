package iclaude.festivaleconomia2019.ui.details.session

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.ItemOrganizerBinding
import iclaude.festivaleconomia2019.model.data_classes.Organizer
import iclaude.festivaleconomia2019.model.data_classes.hasThumbnailUrl
import iclaude.festivaleconomia2019.ui.sessions.sessionInfoTimeDetails
import iclaude.festivaleconomia2019.ui.utils.HeaderGridDrawable

@BindingAdapter("app:sessionImage")
fun imageUrl(imageView: ImageView, imageUrl: String?) {
    if (imageUrl.isNullOrEmpty()) {
        imageView.setImageDrawable(HeaderGridDrawable(imageView.context))
        return
    }

    Glide
        .with(imageView)
        .setDefaultRequestOptions(RequestOptions().placeholder(HeaderGridDrawable(imageView.context)))
        .load(imageUrl)
        .into(imageView)
}

@BindingAdapter("app:startTimestamp", "app:endTimestamp", requireAll = true)
fun timeDetails(textView: TextView, startTimestamp: Long, endTimestamp: Long) {
    textView.text = sessionInfoTimeDetails(textView.context, startTimestamp, endTimestamp)
}

@BindingAdapter("app:eventHeaderAnim")
fun eventHeaderAnim(lottieView: LottieAnimationView, sessionInfo: SessionInfo) {
    val rnd = (1..5).shuffled().first()
    val anim = when (rnd) {
        1 -> "lottie/session_details_header_1.json"
        2 -> "lottie/session_details_header_2.json"
        3 -> "lottie/session_details_header_3.json"
        4 -> "lottie/session_details_header_4.json"
        else -> "lottie/session_details_header_5.json"
    }
    lottieView.setAnimation(anim)
}

@BindingAdapter("app:visibleWithPhotoOrVideo")
fun visibleWithPhotoOrVideo(view: View, sessionInfo: SessionInfo) {
    view.visibility =
        if (sessionInfo.photoUrl.isNullOrEmpty() && sessionInfo.youtubeUrl.isNullOrEmpty()) View.GONE else View.VISIBLE
}

@BindingAdapter("app:goneWithPhotoOrVideo")
fun goneWithPhotoOrVideo(view: View, sessionInfo: SessionInfo) {
    view.visibility =
        if (sessionInfo.photoUrl.isNullOrEmpty() && sessionInfo.youtubeUrl.isNullOrEmpty()) View.VISIBLE else View.GONE
}

@BindingAdapter("app:goneWithPhoto")
fun goneWithPhoto(view: View, sessionInfo: SessionInfo) {
    view.visibility = if (sessionInfo.photoUrl.isNullOrEmpty()) View.VISIBLE else View.GONE
}

@BindingAdapter("app:speakerImage")
fun speakerImage(imageView: ImageView, organizer: Organizer) {
    organizer ?: return

    // Want a 'random' default avatar but should be stable as used on both session details &
    // speaker detail screens (as a shared element transition), so use first initial to pick.
    val placeholderId = when (organizer.name[0].toLowerCase()) {
        in 'a'..'i' -> R.drawable.ic_default_avatar_1
        in 'j'..'r' -> R.drawable.ic_default_avatar_2
        else -> R.drawable.ic_default_avatar_3
    }

    if (organizer.hasThumbnailUrl()) {
        Glide.with(imageView)
            .load(organizer.thumbnailUrl)
            .apply(
                RequestOptions()
                    .placeholder(placeholderId)
                    .circleCrop()
            )
            .into(imageView)
    } else {
        imageView.setImageResource(placeholderId)
    }
}

@BindingAdapter("app:organizers")
fun addOrganizers(layout: LinearLayout, organizers: List<Organizer>) {
    val context = layout.context

    for (organizer in organizers) {
        val binding = ItemOrganizerBinding.inflate(LayoutInflater.from(context), layout, false).apply {
            setOrganizer(organizer)
        }
        layout.addView(binding.root)
    }
}
