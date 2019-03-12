package iclaude.festivaleconomia2019.ui.details.session

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.sessions.sessionInfoTimeDetails

@BindingAdapter("app:sessionImage")
fun imageUrl(imageView: ImageView, imageUrl: String?) {
    val headers = mutableListOf<Int>(
        R.drawable.event_header_1, R.drawable.event_header_2, R.drawable.event_header_3,
        R.drawable.event_header_4, R.drawable.event_header_5, R.drawable.event_header_6
    )
    val index = (0..5).shuffled().first()

    val requestOptions = RequestOptions().apply {
        this.placeholder(headers[index])
    }
    Glide
        .with(imageView.context)
        .setDefaultRequestOptions(requestOptions)
        .load(imageUrl)
        .into(imageView)
}

@BindingAdapter("app:startTimestamp", "app:endTimestamp", requireAll = true)
fun timeDetails(textView: TextView, startTimestamp: Long, endTimestamp: Long) {
    textView.text = sessionInfoTimeDetails(textView.context, startTimestamp, endTimestamp)
}