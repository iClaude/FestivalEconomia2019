package iclaude.festivaleconomia2019.model.di

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import iclaude.festivaleconomia2019.model.repository.EventDataRepository
import iclaude.festivaleconomia2019.ui.ContainerSessionsFragment
import java.io.InputStream
import java.util.*
import javax.inject.Singleton

const val EVENT_DATA_FILE = "event_data_2019.json"
const val EVENT_DATA_FILE_IT = "event_data_2019_it.json"


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
    fun provideFileName(): String {
        return when (Locale.getDefault()) {
            Locale.ITALIAN, Locale.ITALY -> EVENT_DATA_FILE_IT
            else -> EVENT_DATA_FILE
        }
    }

    @Provides
    @Singleton
    fun provideInputStream(context: Context, fileName: String): InputStream =
        context.assets.open(fileName)

    @Provides
    @Singleton
    fun provideRepository(inputStream: InputStream) = EventDataRepository(inputStream)
}

@Component(modules = arrayOf(AppModule::class, RepoModule::class))
@Singleton
interface AppComponent {
    fun inject(fragment: ContainerSessionsFragment)
}