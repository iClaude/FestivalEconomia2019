package iclaude.festivaleconomia2019.model.data_classes

/*
    A user connected to this app with his Google account.
 */
data class User(
    val uid: String = "",
    val starredSessions: List<String> = emptyList()
)