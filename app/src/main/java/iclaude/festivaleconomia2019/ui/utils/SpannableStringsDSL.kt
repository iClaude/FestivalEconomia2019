package iclaude.festivaleconomia2019.ui.utils

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.QuoteSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan

/**
 * Simple DSL to create a SpannableString with various formatting.
 *
 * Usage example:
 * val span = buildSpannablestring {
 *      string = "complete String to format"
 *
 *      span {
 *          start = 10
 *          end = 13
 *          clickableSpan {
 *              context = getContext()
 *              url = myUrl
 *          }
 *      }
 *
 *      span {
 *          start = 20
 *          end = 25
 *
 *          bold()
 *          underline()
 *          increaseSizyBy(1.3f)
 *      }
 *
 *      ...
 * }
 */
fun buildSpannableString(block: MySpannableStringBuilder.() -> Unit): SpannableString =
    MySpannableStringBuilder().apply(block).build()

data class SpanData(
    val start: Int = 0,
    val end: Int = 0,
    val flags: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
    val span: Any
)

class MySpannableStringBuilder {
    var string: String = ""

    private var spans = mutableListOf<SpanData>()

    fun span(block: MySpanDataBuilder.() -> Unit) {
        val data = MySpanDataBuilder().apply(block)
        spans.addAll(data.spans)


    }

    fun build() = SpannableString(string).apply {
        for (span in spans)
            setSpan(span.span, span.start, span.end, span.flags)
    }
}

class MySpanDataBuilder {
    var start: Int = 0
    var end: Int = 0
    var flags: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    var spans = mutableListOf<SpanData>()

    fun clickableSpan(block: MyClickableSpanBuilder.() -> Unit) {
        spans.add(SpanData(start, end, flags, MyClickableSpanBuilder().apply(block).build()))
    }

    fun bold() {
        spans.add(SpanData(start, end, flags, StyleSpan(Typeface.BOLD)))
    }

    fun underline() {
        spans.add(SpanData(start, end, flags, UnderlineSpan()))
    }

    fun italic() {
        spans.add(SpanData(start, end, flags, StyleSpan(Typeface.ITALIC)))
    }

    fun increaseSizeBy(factor: Float) {
        spans.add(SpanData(start, end, flags, RelativeSizeSpan(factor)))
    }

    fun quote(color: Int) {
        spans.add(SpanData(start, end, flags, QuoteSpan(color)))
    }
}

class MyClickableSpanBuilder {
    var context: Context? = null
    var url: String = ""

    fun build() = MyClickableSpan(context!!, url)
}