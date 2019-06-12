package iclaude.festivaleconomia2019.ui.utils

import android.content.Context
import android.content.Intent
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.utils.TAG

/**
 * Utility class to create a clickable link text.
 */

class MyClickableSpan(val context: Context, private val url: String) : ClickableSpan() {
    override fun onClick(widget: View) {
        with(Intent(Intent.ACTION_VIEW)) {
            data = url.toUri()

            try {
                context.startActivity(this)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = ContextCompat.getColor(context, R.color.link)
        ds.isUnderlineText = true
    }
}
