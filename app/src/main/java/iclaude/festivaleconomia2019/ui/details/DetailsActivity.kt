package iclaude.festivaleconomia2019.ui.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import iclaude.festivaleconomia2019.R

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        findNavController(R.id.my_nav_host_fragment).setGraph(R.navigation.details_navigation, intent.extras)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
    }
}
