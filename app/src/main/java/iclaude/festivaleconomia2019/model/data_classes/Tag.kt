package iclaude.festivaleconomia2019.model.data_classes

import android.graphics.Color.parseColor

data class Tag(
    val name: String,
    val fontColor: String,
    val color: String
)

val Tag.colorInt: Int
    get() = parseColor(color)

val Tag.fontColorInt: Int
    get() = parseColor(fontColor)