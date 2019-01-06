package iclaude.festivaleconomia2019.model

/*
    Examples of categories: institutional, event, social, cinema, radio, fun, etc.
    Exampls of tags: technology, jobs, etc.
 */
data class Tag(
    val id: String,
    val category: String,
    val tag: String,
    val backColor: Int,
    val textColor: Int? = null
) {
    // only IDs are used for equality
    override fun equals(other: Any?): Boolean = this === other || (other is Tag && other.id == id)

    fun isUiContentEqual(other: Tag) = backColor == other.backColor && tag == other.tag

    override fun hashCode(): Int = id.hashCode()
}