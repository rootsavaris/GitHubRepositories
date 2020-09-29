package root.savaris.githubrepositories

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import root.savaris.githubrepositories.di.*

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(listOf(networkModule, repositoryModule, databaseModule, viewModelModule, useCaseModule))
        }
    }
}