package iclaude.festivaleconomia2019.ui.sessions.filters

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.sessions.SessionsDisplayInfo

@BindingAdapter("app:filterSet", "app:sessionsFiltered", requireAll = true)
fun title(textView: TextView, filter: Filter?, sessions: List<SessionsDisplayInfo>?) {
    if (filter == null || sessions == null) return
    val str: String
    if (filter.hasTags() || filter.isStarred()) {
        str = textView.context.resources.getQuantityString(R.plurals.filtered_sessions, sessions.size, sessions.size)
    } else {
        str = textView.context.resources.getString(R.string.filters)
    }
    textView.text = str
}

@BindingAdapter("app:toShow")
fun toShow(button: Button, filter: Filter?) {
    Log.d("FILTERS", "toShow called")
    filter?.let {
        if (it.hasTags() || it.isStarred())
            button.visibility = View.VISIBLE
        else
            button.visibility = View.INVISIBLE
    }
}