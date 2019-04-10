package iclaude.festivaleconomia2019.ui.notifications

import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkRequestBuilder {
    // Unique TAG for all requests (tasks).
    private const val NOTIFICATION_TAG = "notification"

    // Data used in the Notification.
    const val SESSION_ID = "id"
    const val SESSION_TITLE = "title"
    const val SESSION_START_TIMESTAMP = "startTimestamp"
    const val SESSION_END_TIMESTAMP = "endTimestamp"
    const val SESSION_DESCRIPTION = "description"
    const val SESSION_ORGANIZERS = "organizers"
    const val SESSION_LOCATION = "location"


    // Create and enqueue a request for a specific Session.
    fun buildNotificationRequest(notificationData: NotificationData) {
        Log.d("MYNOTIFICATIONS", "build notification request with data: ${notificationData.toString()}")

        val inputData = Data.Builder().run {
            putString(SESSION_ID, notificationData.id)
            putString(SESSION_TITLE, notificationData.title)
            putLong(SESSION_START_TIMESTAMP, notificationData.startTimestamp)
            putLong(SESSION_END_TIMESTAMP, notificationData.endTimestamp)
            putString(SESSION_DESCRIPTION, notificationData.description)
            putString(SESSION_ORGANIZERS, notificationData.organizers)
            putString(SESSION_LOCATION, notificationData.location)
            build()
        }

        val notificationWork = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .setInitialDelay(
                notificationData.startTimestamp - 3600000L - System.currentTimeMillis(),
                TimeUnit.MILLISECONDS
            ) // 1 hour before the event starts
            .setInputData(inputData)
            .addTag(NOTIFICATION_TAG)
            .build()

        WorkManager.getInstance().enqueue(notificationWork)
    }

    fun deleteAllRequests() {
        Log.d("MYNOTIFICATIONS", "delete all requests")

        WorkManager.getInstance().cancelAllWorkByTag(NOTIFICATION_TAG)
    }
}