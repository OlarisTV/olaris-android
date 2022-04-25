package tv.olaris.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import tv.olaris.android.di.networkModule
import tv.olaris.android.di.serverDatabaseModule
import tv.olaris.android.di.viewModelsModule

@HiltAndroidApp
class OlarisApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            androidLogger()
            androidContext(this@OlarisApplication)
            modules(
                listOf(
                    serverDatabaseModule,
                    networkModule,
                    viewModelsModule,
                )
            )
        }
    }

    var initialNavigation: Boolean = false

    val applicationScope = CoroutineScope(SupervisorJob())

    companion object {
        private var instance: OlarisApplication? = null

        fun applicationContext(): OlarisApplication {
            return instance as OlarisApplication
        }
    }
}
