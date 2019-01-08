package iclaude.festivaleconomia2019.model

/*
    A user connected to this app with his Google account.
 */
data class User(
    val uid: String = "",
    val devicesTokens: List<String> = emptyList(),
    val starredSessions: List<String>? = null
)