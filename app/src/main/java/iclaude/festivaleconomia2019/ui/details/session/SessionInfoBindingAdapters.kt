package iclaude.festivaleconomia2019.ui.details.session

import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.plusAssign
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.internal.CheckableImageButton
import com.google.firebase.auth.FirebaseAuth
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.ItemOrganizerBinding
import iclaude.festivaleconomia2019.databinding.ItemSessionRelatedBinding
import iclaude.festivaleconomia2019.model.data_classes.*
import iclaude.festivaleconomia2019.ui.details.RelatedSessions
import iclaude.festivaleconomia2019.ui.details.organizer.ImageLoadListener
import iclaude.festivaleconomia2019.ui.details.session.SessionImages.imagesMap
import iclaude.festivaleconomia2019.ui.login.LoginFlow
import iclaude.festivaleconomia2019.ui.utils.*
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_organizer.view.*
import kotlin.math.absoluteValue

/**
 * App bar content.
 */

// Show session's title or generic info in the Toolbar.
@BindingAdapter("app:titleOrInfo")
fun displayTitleOrInfo(textView: TextView, sessionInfo: SessionInfo?) {
    sessionInfo ?: return

    textView.text = if (sessionInfo.photoUrl.isNullOrEmpty() && sessionInfo.youtubeUrl.isNullOrEmpty())
        sessionInfo.title
    else
        textView.context.getString(R.string.session_info_info)
}

@BindingAdapter("app:onOffsetChangedListener")
fun addOnOffsetChangedListener(appBarLayout: AppBarLayout, viewModel: SessionInfoViewModel?) {
    viewModel ?: return

    appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        val percCollapsed = verticalOffset.absoluteValue.toFloat() / appBarLayout.totalScrollRange
        viewModel.setAppBarCollapsedPercentage(percCollapsed)
    })
}

// Title in the Toolbar: displayed when app bar is collapsed.
@BindingAdapter("app:showOrHide")
fun showOrHide(view: View, appBarCollapsedPercentage: Float?) {
    appBarCollapsedPercentage ?: return

    val curAlpha = view.alpha
    val newAlpha = if (appBarCollapsedPercentage > 0.9) 1f else 0f
    if (curAlpha == newAlpha) return

    view.animate().alpha(newAlpha).duration = 300
}

@BindingAdapter("app:sessionImage", "app:viewModel", requireAll = true)
fun sessionImage(imageView: ImageView, sessionInfo: SessionInfo?, viewModel: SessionInfoViewModel?) {
    sessionInfo ?: return
    viewModel ?: return

    if (sessionInfo.photoUrl.isNullOrEmpty()) {
        if (!viewModel.isDarkTheme) {
            imageView.setImageDrawable(HeaderGridDrawable(imageView.context))
        } else {
            val tagStr: String = if (sessionInfo.tags.size > 1) sessionInfo.tags[1].id else sessionInfo.tags[0].id

            Glide
                .with(imageView)
                .load(imagesMap[tagStr])
                .apply(bitmapTransform(BlurTransformation(50)))
                .into(imageView)
        }

        return
    }

    Glide
        .with(imageView)
        .setDefaultRequestOptions(RequestOptions().placeholder(imageView.context.getDrawable(R.drawable.event_header1)))
        .load(sessionInfo.photoUrl)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(imageView)
}

// Lottie animation: there are specific files for light and dark theme.
@BindingAdapter("app:eventHeaderAnim")
fun eventHeaderAnim(lottieView: LottieAnimationView, sessionInfo: SessionInfo?) {
    val fileName = "lottie/${lottieView.context.getString(R.string.lottie_magnifier_filename)}"

    lottieView.setAnimation(fileName)
}

@BindingAdapter("app:visibleWithPhotoOrVideo")
fun visibleWithPhotoOrVideo(view: View, sessionInfo: SessionInfo?) {
    sessionInfo ?: return

    view.visibility =
        if (sessionInfo.photoUrl.isNullOrEmpty() && sessionInfo.youtubeUrl.isNullOrEmpty()) View.GONE else View.VISIBLE
}

@BindingAdapter("app:goneWithPhotoOrVideo")
fun goneWithPhotoOrVideo(view: View, sessionInfo: SessionInfo?) {
    sessionInfo ?: return

    view.visibility =
        if (sessionInfo.photoUrl.isNullOrEmpty() && sessionInfo.youtubeUrl.isNullOrEmpty()) View.VISIBLE else View.GONE
}

@BindingAdapter("app:goneWithPhoto")
fun goneWithPhoto(view: View, sessionInfo: SessionInfo?) {
    sessionInfo ?: return

    view.visibility = if (sessionInfo.photoUrl.isNullOrEmpty()) View.VISIBLE else View.GONE
}


// Time info.
@BindingAdapter("app:startTimestamp", "app:endTimestamp", requireAll = true)
fun timeDetails(textView: TextView, startTimestamp: Long?, endTimestamp: Long?) {
    startTimestamp ?: return
    endTimestamp ?: return

    textView.text =
        sessionInfoTimeDetails(textView.context, startTimestamp, endTimestamp)
}

/**
 *  Organizers.
 */

// Add organizers as child Views of LinearLayout.
@BindingAdapter("app:organizers", "app:viewModel", requireAll = true)
fun addOrganizers(layout: LinearLayout, organizers: List<Organizer>?, viewModel: SessionInfoViewModel?) {
    organizers ?: return
    viewModel ?: return

    val context = layout.context

    for (organizer in organizers) {
        val binding = ItemOrganizerBinding.inflate(LayoutInflater.from(context), layout, false).apply {
            setOrganizer(organizer)
            setViewModel(viewModel)
            ivAvatar.transitionName += organizer.id
        }

        binding.root.setOnClickListener {
            viewModel.goToOrganizer(SharedElementTransitionData(it.ivAvatar, it.ivAvatar.transitionName, organizer.id))
        }

        layout += binding.root
    }
}

@BindingAdapter("app:speakerImage", "app:listener", requireAll = false)
fun speakerImage(imageView: ImageView, organizer: Organizer?, listener: ImageLoadListener?) {
    organizer ?: return

    // Want a 'random' default avatar but should be stable as used on both session details &
    // speaker detail screens (as a shared element transition), so use first initial to pick.
    val placeholderId = when (organizer.name[0].toLowerCase()) {
        in 'a'..'i' -> R.drawable.ic_default_avatar_1
        in 'j'..'r' -> R.drawable.ic_default_avatar_2
        else -> R.drawable.ic_default_avatar_3
    }

    if (organizer.hasThumbnailUrl()) {
        val imageLoad = Glide.with(imageView)
            .load(organizer.thumbnailUrl)
            .override(448, 448)
            .apply(
                RequestOptions()
                    .placeholder(placeholderId)
                    .circleCrop()
            )

        if (listener != null) {
            imageLoad.listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    listener.onImageLoadFailed()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    listener.onImageLoaded()
                    return false
                }
            })
        }

        imageLoad.into(imageView)

    } else {
        imageView.setImageResource(placeholderId)
    }
}

// Click listener for organizer View: go to OrganizerFragment.



/**
 * Related sessions.
 */

@BindingAdapter("app:relatedSessionsVisibility")
fun relatedSessionsVisibility(view: View, sessionInfo: SessionInfo?) {
    sessionInfo ?: return

    view.visibility = if (sessionInfo.hasRelatedSessions()) View.VISIBLE else View.GONE
}

@BindingAdapter("app:relatedSessions", "app:viewModel", requireAll = true)
fun addRelatedSessions(layout: LinearLayout, relatedSessions: List<Session>?, viewModel: SessionInfoViewModel?) {
    relatedSessions ?: return
    viewModel ?: return

    val eventData = viewModel.repository.eventDataLive.value ?: return

    val context = layout.context
    val locations: List<Location> = eventData.locations

    for (session in relatedSessions) {
        val location = locations[session.location.toInt()]
        val binding = ItemSessionRelatedBinding.inflate(LayoutInflater.from(context), layout, false).apply {
            setSession(session)
            setLocation(location)
            setLoginFlow(viewModel)
            setRelatedSessions(viewModel)
        }

        val view = binding.root.apply { tag = session.id }
        layout += view
    }
}

// Icon for livestreamed sessions.
@BindingAdapter("app:liveStreamedVisibility")
fun liveStreamedVisibility(view: View, session: Session?) {
    session ?: return

    view.visibility = if (session.hasYoutubeUrl()) View.VISIBLE else View.GONE

}

// Time and location of each related session.
@BindingAdapter("app:lenLocText", "app:location", requireAll = true)
fun lenLocText(textView: TextView, session: Session?, location: Location?) {
    session ?: return
    location ?: return

    val context = textView.context
    val str = "${getDateShortStr(
        context,
        session.startTimestamp
    )} / ${sessionLength(
        context,
        session.startTimestamp,
        session.endTimestamp
    )} / ${location.name}"
    textView.text = str
}

// Star button to star/unstar each related session.
@BindingAdapter("app:onStarClickListenerRelated", "app:loginFlow", "app:relatedSessions", requireAll = true)
fun onStarClickListener(
    button: CheckableImageButton,
    session: Session?,
    loginFlow: LoginFlow?,
    relatedSessions: RelatedSessions?
) {
    session ?: return
    loginFlow ?: return
    relatedSessions ?: return

    button.setOnClickListener {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val b = it as CheckableImageButton
            b.isChecked = !b.isChecked
            relatedSessions.starOrUnstarSession(session.id, b.isChecked)
        } else {
            loginFlow.startAuthFlow()
        }
    }
}

/**
 * Star button to star/unstar this session.
 */

// Checked or unchecked state.
@BindingAdapter("app:starred")
fun starredSession(button: CheckableImageButton, isStarred: Boolean?) {
    isStarred ?: return

    button.isChecked = isStarred
}

// Click listener.
@BindingAdapter("app:onStarClickListener", "app:viewModel", requireAll = true)
fun onStarFabClicked(
    button: CheckableImageButton,
    sessionId: String?,
    viewModel: SessionInfoViewModel?
) {
    sessionId ?: return
    viewModel ?: return

    button.setOnClickListener { button ->
        if (FirebaseAuth.getInstance().currentUser != null) {
            val starButton = (button as CheckableImageButton).also { it.toggle() }
            viewModel.starOrUnstarSession(sessionId, starButton.isChecked)
        } else {
            viewModel.startAuthFlow()
        }
    }
}


// Link to session's webpage.
@BindingAdapter("app:websiteLink")
fun addWebsiteLink(textView: TextView, url: String?) {
    if (url.isNullOrEmpty()) {
        textView.visibility = View.GONE
        return
    }

    textView.apply {
        text = SpannableString(textView.text).apply {
            setSpan(MyClickableSpan(textView.context, url), 0, textView.text.length, 0)
        }
        movementMethod = LinkMovementMethod.getInstance()
    }
}