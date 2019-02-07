package iclaude.festivaleconomia2019.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.di.App
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

// TODO: landscape layouts
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    @Inject
    lateinit var repository: EventDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        App.component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository.loadEventData()

        val navController = findNavController(R.id.my_nav_host_fragment)
        setupBottomNavMenu(navController)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId != bottomNav.selectedItemId) {

                when (menuItem.itemId) {
                    R.id.containerInfoFragment -> navController.navigate(R.id.action_global_containerInfoFragment)
                    R.id.containerSessionsFragment -> navController.navigate(R.id.action_global_containerSessionsFragment)
                    R.id.mapFragment -> navController.navigate(R.id.action_global_mapFragment)
                }
            }
            true
        }
        bottomNav.menu.findItem(R.id.containerSessionsFragment).setChecked(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.canceLoadingData()
    }
}
