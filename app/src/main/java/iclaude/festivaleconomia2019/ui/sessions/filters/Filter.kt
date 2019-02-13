package iclaude.festivaleconomia2019.ui.sessions.filters

class Filter() {
    var tags: MutableList<String> = mutableListOf()

    fun addTag(tag: String) {
        tags.add(tag)
    }

    fun removeTag(tag: String) {
        tags.remove(tag)
    }

    var starred: Boolean = false
}