package iclaude.festivaleconomia2019.ui

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
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

    }

    private fun setupBottomNavMenu(navController: NavController) {
        bottomNav?.setupWithNavController(navController)
    }

    override fun onStop() {
        super.onStop()

        if (repository.starredSessions.isEmpty()) return

        // TODO: 1) add listener to preference change
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val notificationsEnabled = sharedPref.getBoolean(getString(R.string.info_pref_notifications_key), true)
        if (!notificationsEnabled) return

        WorkRequestBuilder.deleteAllRequests()
        for (sessionId in repository.starredSessions) {
            val session = repository.eventDataLive.value?.sessions?.get(sessionId.toInt())
            if (session != null) WorkRequestBuilder.buildNotificationRequest(session)
        }
    }
}
