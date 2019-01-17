package iclaude.festivaleconomia2019.model.di

import android.app.Application

@Suppress("DEPRECATION")
class App : Application() {

    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = buildComponent()
    }

    fun buildComponent(): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .repoModule(RepoModule())
            .build()

}