package iclaude.festivaleconomia2019.model.di

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

@Suppress("DEPRECATION")
class App : Application() {

    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = buildComponent()
        AndroidThreeTen.init(this)
    }

    fun buildComponent(): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .repoModule(RepoModule())
            .build()

}