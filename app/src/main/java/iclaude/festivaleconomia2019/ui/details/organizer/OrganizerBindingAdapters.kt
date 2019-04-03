package iclaude.festivaleconomia2019.ui.details.organizer

import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.plusAssign
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.databinding.ItemSessionRelatedBinding
import iclaude.festivaleconomia2019.model.data_classes.Location
import iclaude.festivaleconomia2019.model.data_classes.Session
import iclaude.festivaleconomia2019.ui.utils.MyClickableSpan
import kotlin.math.absoluteValue

/**
 * App bar.
 */

// Compute percentage of collapsed app bar.
@BindingAdapter("app:onOffsetChangedListener")
fun addOnOffsetChangedListener(appBarLayout: AppBarLayout, viewModel: OrganizerViewModel) {
    appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        val percCollapsed = verticalOffset.absoluteValue.toFloat() / appBarLayout.totalScrollRange
        viewModel.setAppBarCollapsedPercentage(percCollapsed)
    })
}

@BindingAdapter("app:speakerImage", "app:listener", requireAll = false)
fun speakerImage(imageView: ImageView, organizer: OrganizerInfo?, listener: ImageLoadListener?) {
    organizer ?: return

    // Want a 'random' default avatar but should be stable as used on both session details &
    // speaker detail screens (as a shared element transition), so use first initial to pick.
    val placeholderId = when (organizer.name[0].toLowerCase()) {
        in 'a'..'i' -> R.drawable.ic_default_avatar_1
        in 'j'..'r' -> R.drawable.ic_default_avatar_2
        else -> R.drawable.ic_default_avatar_3
    }

    if (!organizer.thumbnailUrl.isNullOrEmpty()) {
        val imageLoad = Glide.with(imageView)
            .load(organizer.thumbnailUrl)
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

/**
 * Formats a [TextView] to display a [Speaker]'s social links.
 */
@BindingAdapter(value = ["app:websiteUrl", "app:twitterUrl", "linkedInUrl", "facebookUrl"], requireAll = true)
fun createSpeakerLinksView(
    textView: TextView, websiteUrl: String?, twitterUrl: String?, linkedInUrl: String?, facebookUrl: String?
) {
    val links = mapOf(
        R.string.organizer_link_website to websiteUrl,
        R.string.organizer_link_twitter to twitterUrl,
        R.string.organizer_link_linkedin to linkedInUrl,
        R.string.organizer_link_facebook to facebookUrl
    )
        .filterValues {
            !it.isNullOrEmpty()
        }
        .map { (labelRes, url) ->
            val span = SpannableString(textView.context.getString(labelRes))
            span.setSpan(MyClickableSpan(textView.context, url!!), 0, span.length, SPAN_EXCLUSIVE_EXCLUSIVE)
            span
        }
        .joinTo(
            SpannableStringBuilder(),
            separator = " - "
        )

    if (links.isNotBlank()) {
        textView.apply {
            visibility = VISIBLE
            text = links
            // Make links clickable
            movementMethod = LinkMovementMethod.getInstance()
            isFocusable = false
            isClickable = false
        }
    } else {
        textView.visibility = GONE
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

/**
 * An interface for responding to image loading completion.
 */
interface ImageLoadListener {
    fun onImageLoaded()
    fun onImageLoadFailed()
}