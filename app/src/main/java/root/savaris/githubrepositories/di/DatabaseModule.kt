package root.savaris.githubrepositories.di

import android.content.Context
import org.koin.dsl.module
import root.savaris.githubrepositories.framework.database.AppDatabase
import root.savaris.githubrepositories.framework.database.RepositoriesDao

val databaseModule = module {
    single { provideDataBase(get()) }
    factory { provideUserDao(get()) }
}

fun provideDataBase(context: Context): AppDatabase{
    return AppDatabase.getInstance(context)
}

fun provideUserDao(gitHubDatabase: AppDatabase): RepositoriesDao = gitHubDatabase.repositoriesDao()




