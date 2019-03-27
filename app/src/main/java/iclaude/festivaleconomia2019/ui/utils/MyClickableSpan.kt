package iclaude.festivaleconomia2019.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import iclaude.festivaleconomia2019.R

/**
 * Utility class to create a clickable link text.
 */

class MyClickableSpan(val context: Context, private val url: String) : ClickableSpan() {
    override fun onClick(widget: View) {
        with(Intent(Intent.ACTION_VIEW)) {
            data = Uri.parse(url)
            context.startActivity(this)
        }
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = ContextCompat.getColor(context, R.color.link)
        ds.isUnderlineText = true
    }
}
