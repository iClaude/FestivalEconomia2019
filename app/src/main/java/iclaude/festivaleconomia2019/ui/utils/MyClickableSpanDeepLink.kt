package iclaude.festivaleconomia2019.ui.utils

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.info.ContainerInfoFragmentDirections

/**
 * Utility class to create a clickable link text to navigate to a session detail screen.
 */

class MyClickableSpanToSessionDetails(
    private val context: Context,
    private val navController: NavController,
    private val sessionId: String
) : ClickableSpan() {
    override fun onClick(widget: View) {
        ContainerInfoFragmentDirections.actionContainerInfoFragmentToDetailsGraph(sessionId).run {
            navController.navigate(this)
        }
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = ContextCompat.getColor(context, R.color.link)
        //ds.isUnderlineText = true
    }
}
