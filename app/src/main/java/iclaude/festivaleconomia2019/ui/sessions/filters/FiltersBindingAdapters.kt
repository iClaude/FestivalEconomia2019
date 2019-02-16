package iclaude.festivaleconomia2019.ui.sessions.filters

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import iclaude.festivaleconomia2019.R

@BindingAdapter("app:filterSet", "app:sessionsFiltered", requireAll = true)
fun fitleFilter(textView: TextView, filter: Filter, sessions: Int) {
    var str: String = textView.context.resources.getString(R.string.filters)

    if (filter.isFilterSet()) {
        str = textView.context.resources.getQuantityString(R.plurals.filtered_sessions, sessions, sessions)
    }
    textView.text = str
}

@BindingAdapter("app:showWithFilter")
fun showWithFilter(view: View, filter: Filter) {
    if (filter.isFilterSet())
        view.visibility = View.VISIBLE
    else
        view.visibility = View.INVISIBLE

}
