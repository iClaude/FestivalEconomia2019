package iclaude.festivaleconomia2019.ui.sessions

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:lenLoc")
fun lenLoc(textView: TextView, sessionInfo: SessionsDisplayInfo?) {
    if (sessionInfo == null) return
    textView.text = "${sessionLength(
        textView.context,
        sessionInfo.startTimestamp,
        sessionInfo.endTimestamp
    )} / ${sessionInfo.location}"
}