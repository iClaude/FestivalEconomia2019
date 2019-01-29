package iclaude.festivaleconomia2019.ui.sessions

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.BindingAdapter

private const val TAG = "MY_BINDING_ADAPTER"

@BindingAdapter("app:goneWithDataLoaded")
fun goneUnless(view: View, dataLoaded: Boolean) {
    view.visibility = if (dataLoaded) GONE else VISIBLE
}