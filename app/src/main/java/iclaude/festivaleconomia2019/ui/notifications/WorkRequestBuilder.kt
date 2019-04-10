package iclaude.festivaleconomia2019.ui.notifications

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
    const val SESSION_LOCATION_LAT = "locationLat"
    const val SESSION_LOCATION_LNG = "locationLng"


    // Create and enqueue a request for a specific Session.
    fun buildNotificationRequest(notificationData: NotificationData) {
        val inputData = Data.Builder().run {
            putString(SESSION_ID, notificationData.id)
            putString(SESSION_TITLE, notificationData.title)
            putLong(SESSION_START_TIMESTAMP, notificationData.startTimestamp)
            putLong(SESSION_END_TIMESTAMP, notificationData.endTimestamp)
            putString(SESSION_DESCRIPTION, notificationData.description)
            putString(SESSION_ORGANIZERS, notificationData.organizers)
            putString(SESSION_LOCATION, notificationData.location)
            putDouble(SESSION_LOCATION_LAT, notificationData.locationLat)
            putDouble(SESSION_LOCATION_LNG, notificationData.locationLng)
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
        WorkManager.getInstance().cancelAllWorkByTag(NOTIFICATION_TAG)
    }
}