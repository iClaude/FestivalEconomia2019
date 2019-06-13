package iclaude.festivaleconomia2019.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import iclaude.festivaleconomia2019.R

/**
 * Card with a rectangular border with rounded corners used to display info about each type
 * of event.
 */
class EventTypeCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var mContext: Context = context
    private var mAttrs: AttributeSet? = attrs
    private var mDefStyleAttr: Int = 0

    private lateinit var ivHeader: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView


    init {
        mDefStyleAttr = defStyleAttr
        initView()
    }

    private fun initView() {
        inflate(mContext, R.layout.event_type_card, this)
        val typedArray = mContext.obtainStyledAttributes(mAttrs, R.styleable.EventTypeCard, mDefStyleAttr, 0)

        ivHeader = findViewById(R.id.ivHeader)
        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)

        typedArray.getDrawable(R.styleable.EventTypeCard_eventImageSrc)?.let {
            setHeaderImage(it)
        }

        typedArray.getString(R.styleable.EventTypeCard_eventTitle)?.let {
            setTitle(it)
        }

        typedArray.getString(R.styleable.EventTypeCard_eventDescription)?.let {
            setDescription(it)
        }

        typedArray.recycle()
    }

    fun setHeaderImage(image: Drawable) {
        ivHeader.setImageDrawable(image)
    }

    fun setTitle(title: String) {
        tvTitle.text = title
    }

    fun setDescription(description: String) {
        tvDescription.text = description
    }

}