package iclaude.festivaleconomia2019.model.data_classes

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/*
    A single message in the discussion associated with each session.
 */
data class Message(
    val authorName: String = "",
    val authorPhotoUrl: String? = null,
    val message: String = "",
    @ServerTimestamp val time: Date = Date()
)

fun Message.hasPhotoUrl() = authorPhotoUrl?.isNotBlank() ?: false