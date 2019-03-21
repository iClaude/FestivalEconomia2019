package iclaude.festivaleconomia2019.ui.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import androidx.annotation.DrawableRes
import com.google.android.material.floatingactionbutton.FloatingActionButton
import iclaude.festivaleconomia2019.R


/**
 * An extension of FloatingActionButton with checkable state.
 * with this button the user can star/unstar an event, just like a CheckableImageButton.
 */
class StarFab(
    context: Context,
    attrs: AttributeSet
) : FloatingActionButton(context, attrs), Checkable {

    private var _checked = false
        set(value) {
            if (field != value) {
                field = value
                currentDrawable = R.drawable.asld_star_event
                val contentDescRes = if (value) R.string.a11y_starred else R.string.a11y_unstarred
                contentDescription = context.getString(contentDescRes)
                refreshDrawableState()
            }
        }

    @DrawableRes
    private var currentDrawable = 0
        set(value) {
            if (field != value) {
                field = value
                setImageResource(value)
            }
        }

    override fun isChecked() = _checked

    override fun setChecked(checked: Boolean) {
        _checked = checked
    }

    override fun toggle() {
        _checked = !_checked
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        if (!isShowingStar()) {
            return super.onCreateDrawableState(extraSpace)
        }

        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        when {
            isShowingStar() -> {
                val state = if (_checked) stateChecked else stateUnchecked
                mergeDrawableStates(drawableState, state)
            }
        }
        return drawableState
    }

    private fun isShowingStar() = currentDrawable == R.drawable.asld_star_event

    companion object {
        private val stateChecked = intArrayOf(android.R.attr.state_checked)
        private val stateUnchecked = intArrayOf(-android.R.attr.state_checked)
    }
}
