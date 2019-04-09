package iclaude.festivaleconomia2019.ui.notifications

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import iclaude.festivaleconomia2019.model.data_classes.Session
import java.util.concurrent.TimeUnit

object WorkRequestBuilder {
    // Unique TAG for all requests (tasks).
    const val NOTIFICATION_TAG = "notification"

    // Data used in the Notification.
    const val SESSION_TITLE = "title"
    const val SESSION_START_TIMESTAMP = "start"
    const val SESSION_END_TIMESTAMP = "end"
    const val SESSION_DESCRIPTION = "description"
    const val SESSION_ORGANIZERS = "organizers"


    // Create and enqueue a request for a specific Session.
    fun buildNotificationRequest(session: Session) {
        val inputData = Data.Builder().run {
            putString(SESSION_TITLE, session.title)
            putLong(SESSION_START_TIMESTAMP, session.startTimestamp)
            putLong(SESSION_END_TIMESTAMP, session.endTimestamp)
            putString(SESSION_DESCRIPTION, session.description)
            putString(SESSION_ORGANIZERS, session.organizers.joinToString(" - ") { it })
            build()
        }

        val notificationWork = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .setInitialDelay(
                session.startTimestamp - 3600000L - System.currentTimeMillis(),
                TimeUnit.MILLISECONDS
            ) // 1 hour before the event starts
            .setInputData(inputData)
            .addTag(NOTIFICATION_TAG)
            .build()

        WorkManager.getInstance().enqueue(notificationWork)
    }

    fun deleteAllRequests() {
        WorkManager.getInstance().cancelAllWorkByTag(NOTIFICATION_TAG)
    }
}