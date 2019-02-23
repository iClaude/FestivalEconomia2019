package iclaude.festivaleconomia2019.ui.sessions.filters

import iclaude.festivaleconomia2019.model.data_classes.Tag

data class Filter(
    var tagsTypes: MutableList<Tag> = mutableListOf(),
    var tagsTopics: MutableList<Tag> = mutableListOf(),
    var starred: Boolean = false
)

fun Filter.hasTypeTags() = tagsTypes.isNotEmpty()

fun Filter.hasTopicTags() = tagsTopics.isNotEmpty()

fun Filter.hasTags() = hasTypeTags().or(hasTopicTags())

fun Filter.isStarred() = starred

fun Filter.isFilterSet() = hasTags() || isStarred()



