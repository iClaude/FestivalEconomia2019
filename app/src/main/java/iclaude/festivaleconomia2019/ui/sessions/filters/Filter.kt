package iclaude.festivaleconomia2019.ui.sessions.filters

import org.threeten.bp.ZonedDateTime

data class Filter(
    var day: ZonedDateTime? = null,
    var tags: MutableList<String> = mutableListOf(),
    var starred: Boolean = false
)



