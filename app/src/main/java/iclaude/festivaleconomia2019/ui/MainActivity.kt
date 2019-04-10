package iclaude.festivaleconomia2019.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import iclaude.festivaleconomia2019.ui.notifications.NOTIFICATION_CHANNEL_ID
import iclaude.festivaleconomia2019.ui.notifications.NotificationData
import iclaude.festivaleconomia2019.ui.notifications.WorkRequestBuilder
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

// TODO: landscape layouts
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: EventDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        App.component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.my_nav_host_fragment)
        setupBottomNavMenu(navController)

        createNotificationChannel()
    }

    private fun setupBottomNavMenu(navController: NavController) {
        bottomNav?.setupWithNavController(navController)
    }

    override fun onStop() {
        super.onStop()

        // Notifications: create requests for WorkManager.
        val sessions = repository.eventDataLive.value?.sessions ?: return
        val locations = repository.eventDataLive.value?.locations ?: return
        val organizers = repository.eventDataLive.value?.organizers ?: return
        if (repository.starredSessions.isEmpty()) return

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val notificationsEnabled = sharedPref.getBoolean(getString(R.string.info_pref_notifications_key), true)
        if (!notificationsEnabled) return

        Log.d("MYNOTIFICATIONS", "Main Activity: create requests for notifications")

        WorkRequestBuilder.deleteAllRequests()
        for (sessionId in repository.starredSessions) {
            val session = sessions[sessionId.toInt()]
            val locationStr = locations[session.location.toInt()].name
            val organizersStr = organizers.filter {
                it.id in session.organizers
            }.joinToString(" - ") { it.name }
            if (session != null) WorkRequestBuilder.buildNotificationRequest(
                NotificationData(
                    session.id, session.title,
                    session.startTimestamp, session.endTimestamp, session.description, organizersStr, locationStr
                )
            )
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel.
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                lightColor = getColor(R.color.primaryColor)
            }

            /*Register the channel with the system; you can't change the importance
            or other notification behaviors after this*/
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}
