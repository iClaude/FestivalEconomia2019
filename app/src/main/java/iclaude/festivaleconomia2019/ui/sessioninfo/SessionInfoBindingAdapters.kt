package iclaude.festivaleconomia2019.ui.sessioninfo

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import iclaude.festivaleconomia2019.R

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