package iclaude.festivaleconomia2019.ui.sessions

import android.content.Context
import android.graphics.Color.TRANSPARENT
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.internal.CheckableImageButton
import com.google.firebase.auth.FirebaseAuth
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.data_classes.Tag

private const val TAG = "TAGS"

@BindingAdapter("app:refreshListener")
fun addRefreshListener(swipeRefreshLayout: SwipeRefreshLayout, viewModel: SessionListViewModel) {
    swipeRefreshLayout.setColorSchemeResources(R.color.primaryColor)
    swipeRefreshLayout.setOnRefreshListener {
        if (FirebaseAuth.getInstance().currentUser != null)
            viewModel.updateSessionListWithStarredSessions()
        else
            swipeRefreshLayout.isRefreshing = false
    }
}

@BindingAdapter("app:sessionTags")
fun sessionTags(recyclerView: RecyclerView, sessionTags: List<Tag>?) {
    recyclerView.adapter = (recyclerView.adapter as? TagAdapter ?: TagAdapter())
        .apply {
            tags = sessionTags ?: emptyList()
        }
}

@BindingAdapter("app:tagTint")
fun tagTint(textView: TextView, color: Int) {
    // Tint the colored dot
    (textView.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(
        tagTintOrDefault(
            color,
            textView.context
        )
    )
}

@BindingAdapter("app:onStarClickListener", "app:viewModel", requireAll = true)
fun onStarClickListener(
    button: CheckableImageButton,
    sessionInfo: SessionInfoForList,
    viewModel: SessionListViewModel
) {
    button.setOnClickListener {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val b = it as CheckableImageButton
            b.isChecked = !b.isChecked
            viewModel.starOrUnstarSession(sessionInfo.id, b.isChecked)
        } else {
            viewModel.startAuthFlow()
        }
    }
}

fun tagTintOrDefault(color: Int, context: Context): Int {
    return if (color != TRANSPARENT) {
        color
    } else {
        ContextCompat.getColor(context, R.color.default_tag_color)
    }
}