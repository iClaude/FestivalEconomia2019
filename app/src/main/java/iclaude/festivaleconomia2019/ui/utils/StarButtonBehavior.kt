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
        val appBarMinHeight = appBarLayout.minimumHeightForVisibleOverlappingContent.toFloat() * 2 / 3

        child.apply {
            alpha = 0 + (tmpRect.bottom - appBarMinHeight) / (appBarLayout.height - appBarMinHeight)
            isEnabled = this.alpha > 0.5
        }

        return true
    }
}