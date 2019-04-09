package iclaude.festivaleconomia2019.ui.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.SESSION_DESCRIPTION
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.SESSION_END_TIMESTAMP
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.SESSION_ORGANIZERS
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.SESSION_START_TIMESTAMP
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.SESSION_TITLE

class NotifyWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val notificationData = NotificationData(
            inputData.getString(SESSION_TITLE),
            inputData.getLong(SESSION_START_TIMESTAMP, 0),
            inputData.getLong(SESSION_END_TIMESTAMP, 0),
            inputData.getString(SESSION_DESCRIPTION),
            inputData.getString(SESSION_ORGANIZERS)
        )
        triggerNotification(notificationData)

        return Result.success()
    }

    private fun triggerNotification(notificationData: NotificationData) {
        // We must have valid data at this point to create a Notification.
        requireNotNull(notificationData.title) { "Error: notificationData.title is null" }
        requireNotNull(notificationData.description) { "Error: notificationData.description is null" }
        requireNotNull(notificationData.organizers) { "Error: notificationData.organizers is null" }
        require(notificationData.startTimestamp > 0) { "Error: notificationData.startTimestamp is 0" }
        require(notificationData.endTimestamp > 0) { "Error: notificationData.endTimestamp is 0" }

    }

    private class NotificationData(
        val title: String?,
        val startTimestamp: Long,
        val endTimestamp: Long,
        val description: String?,
        val organizers: String?
    )
}