package root.savaris.githubrepositories.utils

import android.app.Application
import android.content.Context
import root.savaris.githubrepositories.data.Repository
import root.savaris.githubrepositories.data.local.AppDatabase
import root.savaris.githubrepositories.data.remote.NetworkService
import root.savaris.githubrepositories.data.remote.RemoteRepository
import root.savaris.githubrepositories.data.remote.rest_utils.AppExecutors
import root.savaris.githubrepositories.detail.DetailRepositoryViewModelFactory
import root.savaris.githubrepositories.favorites.ListRepositoriesFavoritesViewModelFactory
import root.savaris.githubrepositories.list.ListRepositoriesViewModelFactory

interface ViewModelFactoryProvider {
    fun provideListRepositoriesViewModelFactory(context: Context): ListRepositoriesViewModelFactory
    fun provideListRepositoriesFavoritesViewModelFactory(context: Context): ListRepositoriesFavoritesViewModelFactory
    fun provideDetailRepositoryViewModelFactory(context: Context, owner: String, repositoryName: String): DetailRepositoryViewModelFactory
}

val Injector: ViewModelFactoryProvider
    get() = currentInjector

private object DefaultViewModelProvider: ViewModelFactoryProvider {
    private fun getRepository(context: Context): Repository {
        return Repository.getInstance(
            context,
            repositoryDao(context),
            AppExecutors(),
            remoteRepository()
        )
    }

    private fun networkService() = NetworkService()
    private fun remoteRepository() = RemoteRepository(networkService())

    private fun repositoryDao(context: Context) =
        AppDatabase.getInstance(context.applicationContext).repositoriesDao()

    override fun provideListRepositoriesViewModelFactory(context: Context): ListRepositoriesViewModelFactory {
        val repository = getRepository(context)
        return ListRepositoriesViewModelFactory(context.applicationContext as Application, repository)
    }

    override fun provideListRepositoriesFavoritesViewModelFactory(context: Context): ListRepositoriesFavoritesViewModelFactory {
        val repository = getRepository(context)
        return ListRepositoriesFavoritesViewModelFactory(context.applicationContext as Application, repository)
    }

    override fun provideDetailRepositoryViewModelFactory(context: Context, owner: String, repositoryName: String): DetailRepositoryViewModelFactory {
        val repository = getRepository(context)
        return DetailRepositoryViewModelFactory(context.applicationContext as Application, repository, owner, repositoryName)
    }
}

private object Lock

@Volatile private var currentInjector: ViewModelFactoryProvider =
    DefaultViewModelProvider

