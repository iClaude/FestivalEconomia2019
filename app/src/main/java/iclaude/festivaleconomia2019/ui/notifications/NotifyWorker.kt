package iclaude.festivaleconomia2019.ui.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.NOTIFICATION_ORGANIZER_AVATAR_URL
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.NOTIFICATION_SESSION_DESCRIPTION
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.NOTIFICATION_SESSION_END_TIMESTAMP
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.NOTIFICATION_SESSION_ID
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.NOTIFICATION_SESSION_LOCATION
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.NOTIFICATION_SESSION_LOCATION_LAT
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.NOTIFICATION_SESSION_LOCATION_LNG
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.NOTIFICATION_SESSION_ORGANIZERS
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.NOTIFICATION_SESSION_START_TIMESTAMP
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder.NOTIFICATION_SESSION_TITLE
import iclaude.festivaleconomia2019.ui.utils.buildSpannableString
import iclaude.festivaleconomia2019.ui.utils.sessionInfoTimeStartDetails

const val NOTIFICATION_CHANNEL_ID = "events_notification_channel"

class NotifyWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val notificationData = NotificationData(
            inputData.getString(NOTIFICATION_SESSION_ID),
            inputData.getString(NOTIFICATION_SESSION_TITLE),
            inputData.getLong(NOTIFICATION_SESSION_START_TIMESTAMP, 0),
            inputData.getLong(NOTIFICATION_SESSION_END_TIMESTAMP, 0),
            inputData.getString(NOTIFICATION_SESSION_DESCRIPTION),
            inputData.getString(NOTIFICATION_SESSION_ORGANIZERS),
            inputData.getString(NOTIFICATION_SESSION_LOCATION),
            inputData.getDouble(NOTIFICATION_SESSION_LOCATION_LAT, 0.0),
            inputData.getDouble(NOTIFICATION_SESSION_LOCATION_LNG, 0.0),
            inputData.getString(NOTIFICATION_ORGANIZER_AVATAR_URL)
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
        val sessionPendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.main_navigation)
            .setDestination(R.id.sessionInfoFragment)
            .setArguments(args)
            .createPendingIntent()

        // PendingIntent to start Google Maps to obtain directions to the selected location.
        val uri = "google.navigation:q=${notificationData.locationLat},${notificationData.locationLng}".toUri()
        val gMapsIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            `package` = "com.google.android.apps.maps"
        }
        val directionsPendingIntent = PendingIntent.getActivity(applicationContext, 0, gMapsIntent, 0)

        // Notification data.
        val speakers = applicationContext.getString(R.string.notification_speakers)
        val description = notificationData.description.take(200)
        val bigText = "${sessionInfoTimeStartDetails(
            applicationContext,
            notificationData.startTimestamp
        )} / ${notificationData.location}\n\n${description}...\n" +
                "$speakers: ${notificationData.organizers}"

        val bigTextSpan = buildSpannableString {
            string = bigText

            span {
                start = bigText.indexOf(description)
                end = start + description.length
                italic()
            }

            span {
                start = bigText.indexOf(speakers)
                end = start + speakers.length
                increaseSizeBy(1.3f)
            }

            span {
                start = bigText.indexOf(speakers) + speakers.length + 2
                end = bigText.length
                bold()
            }
        }

        val builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notificationData.title)
            .setContentText(
                "${sessionInfoTimeStartDetails(
                    applicationContext,
                    notificationData.startTimestamp
                )} / ${notificationData.location}"
            )
            .setColor(ContextCompat.getColor(applicationContext, R.color.primaryColor))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(VISIBILITY_PUBLIC)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bigTextSpan)
            )
            .setContentIntent(sessionPendingIntent)
            .addAction(
                R.drawable.ic_directions_black_24dp,
                applicationContext.getString(R.string.notification_directions),
                directionsPendingIntent
            )
            .setAutoCancel(true)

        // Avatar of organizer.
        if (!notificationData.avatarUrl.isNullOrEmpty()) {
            val futureTarget = Glide.with(applicationContext)
                .asBitmap()
                .load(notificationData.avatarUrl)
                .circleCrop()
                .submit()

            val avatar = futureTarget.get()
            builder.setLargeIcon(avatar)
        }

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
    val location: String?,
    val locationLat: Double,
    val locationLng: Double,
    val avatarUrl: String?
)