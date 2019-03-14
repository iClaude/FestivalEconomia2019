package iclaude.festivaleconomia2019.ui.details.session

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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