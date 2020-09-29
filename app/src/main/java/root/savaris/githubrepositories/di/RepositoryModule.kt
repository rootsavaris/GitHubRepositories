package root.savaris.githubrepositories.di

import org.koin.dsl.module
import root.savaris.githubrepositories.data.ILocalDataSource
import root.savaris.githubrepositories.data.IRemoteDataSource
import root.savaris.githubrepositories.data.IRepository
import root.savaris.githubrepositories.data.Repository
import root.savaris.githubrepositories.framework.database.LocalDataSource
import root.savaris.githubrepositories.framework.database.RepositoriesDao
import root.savaris.githubrepositories.framework.network.RemoteDataSource
import root.savaris.githubrepositories.framework.network.RepositoriesServiceApi

val repositoryModule = module {
    factory { provideLocalDataSource(get()) }
    factory { provideRemoteDataSource(get()) }
    single { provideRepository(get(), get()) }
}

fun provideRepository(iLocalDataSource: ILocalDataSource, iRemoteDataSource: IRemoteDataSource): IRepository {
    return Repository(iLocalDataSource, iRemoteDataSource)
}

fun provideLocalDataSource(repositoriesDao: RepositoriesDao): ILocalDataSource{
    return LocalDataSource(repositoriesDao)
}

fun provideRemoteDataSource(repositoriesServiceApi: RepositoriesServiceApi): IRemoteDataSource{
    return RemoteDataSource(repositoriesServiceApi)
}
