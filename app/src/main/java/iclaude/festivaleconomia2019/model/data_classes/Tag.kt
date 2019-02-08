package iclaude.festivaleconomia2019.model.data_classes

import android.graphics.Color.parseColor

data class Tag(
    val id: String,
    val category: String,
    val name: String,
    val fontColor: String,
    val color: String
) {
    companion object {
        const val CATEGORY_TYPE = "type"
        const val CATEGORY_TOPIC = "topic"
    }
}

val Tag.colorInt: Int
    get() = parseColor(color)

val Tag.fontColorInt: Int
    get() = parseColor(fontColor)

fun Tag.isUiContentEqual(other: Tag) = color == other.color && name == other.name