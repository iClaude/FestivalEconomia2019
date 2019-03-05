package iclaude.festivaleconomia2019.model.di

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import iclaude.festivaleconomia2019.R
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import iclaude.festivaleconomia2019.ui.MainActivity
import iclaude.festivaleconomia2019.ui.map.MapViewModel
import iclaude.festivaleconomia2019.ui.sessions.SessionListViewModel
import java.io.InputStream
import javax.inject.Singleton


@Module
class AppModule(private val appContext: Context) {

    @Provides
    @Singleton
    fun provideContext(): Context = appContext
}

@Module
class RepoModule {

    @Provides
    @Singleton
    fun provideInputStream(context: Context): InputStream =
        context.resources.openRawResource(R.raw.event_data_2019)

    @Provides
    @Singleton
    fun provideRepository(inputStream: InputStream): EventDataRepository = EventDataRepository(inputStream)
}

@Component(modules = arrayOf(AppModule::class, RepoModule::class))
@Singleton
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(mapViewModel: MapViewModel)
    fun inject(sessionsViewModel: SessionListViewModel)
}