package iclaude.festivaleconomia2019.model.di

import android.app.Application
import androidx.preference.PreferenceManager
import com.jakewharton.threetenabp.AndroidThreeTen
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.ui.info.preferences.setNightMode

@Suppress("DEPRECATION")
class App : Application() {

    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        component = buildComponent()
        AndroidThreeTen.init(this)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.getString(getString(R.string.info_pref_theme_key), "0")?.toInt()?.let {
            setNightMode(it)
        }
    }

    fun buildComponent(): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .repoModule(RepoModule())
            .build()

}