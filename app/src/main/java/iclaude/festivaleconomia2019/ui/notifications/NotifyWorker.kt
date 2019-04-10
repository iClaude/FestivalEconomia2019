package iclaude.festivaleconomia2019.ui.notifications

import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.SESSION_DESCRIPTION
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.SESSION_END_TIMESTAMP
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.SESSION_ID
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.SESSION_LOCATION
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.SESSION_ORGANIZERS
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.SESSION_START_TIMESTAMP
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.SESSION_TITLE
import iclaude.festivaleconomia2019.ui.utils.sessionInfoTimeStartDetails

const val NOTIFICATION_CHANNEL_ID = "events_notification_channel"

class NotifyWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val notificationData = NotificationData(
            inputData.getString(SESSION_ID),
            inputData.getString(SESSION_TITLE),
            inputData.getLong(SESSION_START_TIMESTAMP, 0),
            inputData.getLong(SESSION_END_TIMESTAMP, 0),
            inputData.getString(SESSION_DESCRIPTION),
            inputData.getString(SESSION_ORGANIZERS),
            inputData.getString(SESSION_LOCATION)
        )
        triggerNotification(notificationData)

        return Result.success()
    }

    private fun triggerNotification(notificationData: NotificationData) {
        // We must have valid data at this point to create a Notification.
        requireNotNull(notificationData.id) { "Error: notificationData.id is null" }
        requireNotNull(notificationData.title) { "Error: notificationData.title is null" }
        requireNotNull(notificationData.description) { "Error: notificationData.description is null" }
        requireNotNull(notificationData.organizers) { "Error: notificationData.organizers is null" }
        require(notificationData.startTimestamp > 0) { "Error: notificationData.startTimestamp is 0" }
        require(notificationData.endTimestamp > 0) { "Error: notificationData.endTimestamp is 0" }

        // PendingIntent to app's session info page.
        val args = Bundle().apply {
            putString("sessionId", notificationData.id)
        }
        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.main_navigation)
            .setDestination(R.id.detailsGraph)
            .setArguments(args)
            .createPendingIntent()

        // Notification data.
        val bitText = "${notificationData.description.take(50)}...<br>${notificationData.organizers}"
        val builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_squirrel_round)
            .setContentTitle(notificationData.title)
            .setContentText(
                "${sessionInfoTimeStartDetails(
                    applicationContext,
                    notificationData.startTimestamp
                )} / ${notificationData.location}"
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(VISIBILITY_PUBLIC)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notificationData.description)
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Issue the notification.
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(notificationData.id.toInt(), builder.build())
        }
    }
}

class NotificationData(
    val id: String?,
    val title: String?,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val description: String?,
    val organizers: String?,
    val location: String?
)