package iclaude.festivaleconomia2019.ui.utils

import android.app.Activity
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import androidx.core.view.postDelayed
import androidx.databinding.ViewDataBinding


inline fun <T : ViewDataBinding> T.executeAfter(block: T.() -> Unit) {
    block()
    executePendingBindings()
}

/**
 * An extension to `postponeEnterTransition` which will resume after a timeout.
 */
fun Activity.postponeEnterTransition(timeout: Long) {
    postponeEnterTransition()
    window.decorView.postDelayed(timeout) {
        startPostponedEnterTransition()
    }
}


/**
 * Simple DSL to create a SpannableString with clickable links within it.
 */
fun buildSpannableString(block: MySpannableStringBuilder.() -> Unit): SpannableString =
    MySpannableStringBuilder().apply(block).build()

class MySpannableStringBuilder {
    var string: String = ""

    private var start: Int = 0
    private var end: Int = 0
    private var flags: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    private var span: Any? = null

    fun span(block: MySpanDataBuilder.() -> Unit) {
        val data = MySpanDataBuilder().apply(block)
        start = data.start
        end = data.end
        flags = data.flags
        span = data.span
    }

    fun build() = SpannableString(string).apply {
        setSpan(span, start, end, flags)
    }
}

class MySpanDataBuilder {
    var start: Int = 0
    var end: Int = 0
    var flags: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    var span: Any? = null

    fun clickableSpan(block: MyClickableSpanBuilder.() -> Unit) {
        span = MyClickableSpanBuilder().apply(block).build()
    }
}

class MyClickableSpanBuilder {
    var context: Context? = null
    var url: String = ""

    fun build() = MyClickableSpan(context!!, url)
}


