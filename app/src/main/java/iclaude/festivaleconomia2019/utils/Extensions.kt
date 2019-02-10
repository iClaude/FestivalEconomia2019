package iclaude.festivaleconomia2019.utils

import android.text.StaticLayout
import android.util.Log
import androidx.databinding.ObservableBoolean
import iclaude.festivaleconomia2019.BuildConfig

fun ObservableBoolean.hasSameValue(other: ObservableBoolean) = get() == other.get()

/**
 * Calculated the widest line in a [StaticLayout].
 */
fun StaticLayout.textWidth(): Int {
    var width = 0f
    for (i in 0 until lineCount) {
        width = width.coerceAtLeast(getLineWidth(i))
    }
    return width.toInt()
}

/**
 * Linearly interpolate between two values.
 */
fun lerp(a: Float, b: Float, t: Float): Float {
    return a + (b - a) * t
}

/**
 * Helper to throw exceptions only in Debug builds, logging a warning otherwise.
 */
fun exceptionInDebug(t: Throwable) {
    if (BuildConfig.DEBUG) {
        throw t
    } else {
        Log.e("EXCEPTION", t.toString())
    }
}