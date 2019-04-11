package iclaude.festivaleconomia2019.ui.notifications

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkRequestBuilder {
    // Unique TAG for all requests (tasks).
    private const val NOTIFICATION_TAG = "notification"

    // Data used in the Notification.
    const val NOTIFICATION_SESSION_ID = "id"
    const val NOTIFICATION_SESSION_TITLE = "title"
    const val NOTIFICATION_SESSION_START_TIMESTAMP = "startTimestamp"
    const val NOTIFICATION_SESSION_END_TIMESTAMP = "endTimestamp"
    const val NOTIFICATION_SESSION_DESCRIPTION = "description"
    const val NOTIFICATION_SESSION_ORGANIZERS = "organizers"
    const val NOTIFICATION_SESSION_LOCATION = "location"
    const val NOTIFICATION_SESSION_LOCATION_LAT = "locationLat"
    const val NOTIFICATION_SESSION_LOCATION_LNG = "locationLng"
    const val NOTIFICATION_ORGANIZER_AVATAR_URL = "avatarUrl"


    // Create and enqueue a request for a specific Session.
    fun buildNotificationRequest(notificationData: NotificationData) {
        val inputData = Data.Builder().run {
            putString(NOTIFICATION_SESSION_ID, notificationData.id)
            putString(NOTIFICATION_SESSION_TITLE, notificationData.title)
            putLong(NOTIFICATION_SESSION_START_TIMESTAMP, notificationData.startTimestamp)
            putLong(NOTIFICATION_SESSION_END_TIMESTAMP, notificationData.endTimestamp)
            putString(NOTIFICATION_SESSION_DESCRIPTION, notificationData.description)
            putString(NOTIFICATION_SESSION_ORGANIZERS, notificationData.organizers)
            putString(NOTIFICATION_SESSION_LOCATION, notificationData.location)
            putDouble(NOTIFICATION_SESSION_LOCATION_LAT, notificationData.locationLat)
            putDouble(NOTIFICATION_SESSION_LOCATION_LNG, notificationData.locationLng)
            putString(NOTIFICATION_ORGANIZER_AVATAR_URL, notificationData.avatarUrl)
            build()
        }

        val initialDelay =
            notificationData.startTimestamp - 3600000L - System.currentTimeMillis() // 1 hour before the event starts
        if (initialDelay < 0) return

        val notificationWorkBuilder = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .setInputData(inputData)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(NOTIFICATION_TAG)

        WorkManager.getInstance().enqueue(notificationWorkBuilder.build())
    }

    fun deleteAllRequests() {
        WorkManager.getInstance().cancelAllWorkByTag(NOTIFICATION_TAG)
    }
}