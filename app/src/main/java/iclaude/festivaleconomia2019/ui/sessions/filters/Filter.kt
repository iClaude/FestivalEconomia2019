package iclaude.festivaleconomia2019.ui.sessions.filters

import iclaude.festivaleconomia2019.model.data_classes.Tag

data class Filter(
    var tags: MutableList<Tag> = mutableListOf(),
    var starred: Boolean = false
)

fun Filter.hasTags() = tags.isNotEmpty()

fun Filter.isStarred() = starred



