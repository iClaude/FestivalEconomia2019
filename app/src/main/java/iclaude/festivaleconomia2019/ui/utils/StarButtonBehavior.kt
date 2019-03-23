package iclaude.festivaleconomia2019.ui.utils

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.coordinatorlayout.widget.ViewGroupUtils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.internal.CheckableImageButton

class StarButtonBehavior : CoordinatorLayout.Behavior<CheckableImageButton> {
    constructor() : super()

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private val tmpRect = Rect()

    private var isAppBarExpandedHeightSet: Boolean = false
    private var appBarExpandedHeight: Int = 0
        set(value) {
            if (!isAppBarExpandedHeightSet) {
                field = value
                isAppBarExpandedHeightSet = true
            }
        }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: CheckableImageButton, dependency: View): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: CheckableImageButton,
        dependency: View
    ): Boolean {
        val appBarLayout = dependency as? AppBarLayout
        appBarLayout ?: return false

        ViewGroupUtils.getDescendantRect(parent, appBarLayout, tmpRect)
        appBarExpandedHeight = tmpRect.bottom
        val appBarMinHeight = appBarLayout.minimumHeightForVisibleOverlappingContent.toFloat() * 2 / 3

        child.apply {
            alpha = ((tmpRect.bottom - appBarMinHeight) / (appBarExpandedHeight - appBarMinHeight)).coerceAtLeast(0f)
            isEnabled = this.alpha > 0.5
        }

        return true
    }
}