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
import com.google.android.material.internal.CheckableImageButton
import com.google.firebase.auth.FirebaseAuth
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.ItemOrganizerBinding
import iclaude.festivaleconomia2019.databinding.ItemSessionRelatedBinding
import iclaude.festivaleconomia2019.model.data_classes.*
import iclaude.festivaleconomia2019.ui.sessions.getDateShortStr
import iclaude.festivaleconomia2019.ui.sessions.sessionInfoTimeDetails
import iclaude.festivaleconomia2019.ui.sessions.sessionLength
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
    val rnd = (1..6).shuffled().first()

    lottieView.setAnimation(
        when (rnd) {
        1 -> "lottie/eco1.json"
        2 -> "lottie/eco2.json"
        3 -> "lottie/eco3.json"
        4 -> "lottie/eco4.json"
            else -> "lottie/eco5.json"
        }
    )
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
fun speakerImage(imageView: ImageView, organizer: Organizer?) {
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

@BindingAdapter("app:relatedSessionsVisibility")
fun relatedSessionsVisibility(view: View, sessionInfo: SessionInfo) {
    view.visibility = if (sessionInfo.hasRelatedSessions()) View.VISIBLE else View.GONE
}

@BindingAdapter("app:relatedSessions", "app:viewModel", requireAll = true)
fun addRelatedSessions(layout: LinearLayout, relatedSessions: List<Session>, viewModel: SessionInfoViewModel) {
    val eventData = viewModel.repository.eventDataLive.value ?: return

    val context = layout.context
    val locations: List<Location> = eventData.locations

    for (session in relatedSessions) {
        val location = locations[session.location.toInt()]
        val binding = ItemSessionRelatedBinding.inflate(LayoutInflater.from(context), layout, false).apply {
            setSession(session)
            setLocation(location)
            setViewModel(viewModel)
        }

        val view = binding.root.apply { tag = session.id }
        layout.addView(view)
    }
}

@BindingAdapter("app:liveStreamedVisibility")
fun liveStreamedVisibility(view: View, session: Session) {
    view.visibility = if (session.hasYoutubeUrl()) View.VISIBLE else View.GONE

}

@BindingAdapter("app:lenLocText", "app:location", requireAll = true)
fun lenLocText(textView: TextView, session: Session, location: Location) {
    val context = textView.context
    val str = "${getDateShortStr(context, session.startTimestamp)} / ${sessionLength(
        context,
        session.startTimestamp,
        session.endTimestamp
    )} / ${location.name}"
    textView.text = str
}

@BindingAdapter("app:onStarClickListenerRelated", "app:viewModel", requireAll = true)
fun onStarClickListener(
    button: CheckableImageButton,
    session: Session,
    viewModel: SessionInfoViewModel
) {
    button.setOnClickListener {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val b = it as CheckableImageButton
            b.isChecked = !b.isChecked
            viewModel.starOrUnstarSession(session.id, b.isChecked)
        } else {
            viewModel.startAuthFlow()
        }
    }
}

// Star button to star/unstar this session.

// Checked or unchecked state.
@BindingAdapter("app:starred")
fun starredSession(button: CheckableImageButton, isStarred: Boolean) {
    button.isChecked = isStarred
}

// Click listener.
@BindingAdapter("app:onStarClickListener", "app:viewModel", requireAll = true)
fun onStarFabClicked(
    button: CheckableImageButton,
    sessionId: String,
    viewModel: SessionInfoViewModel
) {
    button.setOnClickListener { button ->
        if (FirebaseAuth.getInstance().currentUser != null) {
            val starButton = (button as CheckableImageButton).also { it.toggle() }
            viewModel.starOrUnstarSession(sessionId, starButton.isChecked)
        } else {
            viewModel.startAuthFlow()
        }
    }
}