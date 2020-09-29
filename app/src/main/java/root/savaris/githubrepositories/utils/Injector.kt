package root.savaris.githubrepositories.utils

import android.app.Application
import android.content.Context
import kotlinx.coroutines.flow.Flow
import root.savaris.githubrepositories.data.ILocalDataSource
import root.savaris.githubrepositories.data.IRemoteDataSource
import root.savaris.githubrepositories.data.IRepository
import root.savaris.githubrepositories.data.Repository
import root.savaris.githubrepositories.framework.database.AppDatabase
import root.savaris.githubrepositories.domain.RepositoryItem
import root.savaris.githubrepositories.framework.network.model.ApiResponse
import root.savaris.githubrepositories.framework.network.model.RepositoryListNetwork
import root.savaris.githubrepositories.framework.network.model.RepositoryNetwork
import root.savaris.githubrepositories.presentation.favorites.ListRepositoriesFavoritesViewModelFactory

interface ViewModelFactoryProvider {
    fun provideListRepositoriesFavoritesViewModelFactory(context: Context): ListRepositoriesFavoritesViewModelFactory
}

val Injector: ViewModelFactoryProvider
    get() = currentInjector

private object DefaultViewModelProvider: ViewModelFactoryProvider {
    private fun getRepository(context: Context): Repository {
        return Repository(Local2(), Remote2())
/*
        return Repository().getInstance(
            context,
            repositoryDao(context),
            AppExecutors(),
            remoteRepository()
        )
 */
    }

    private fun repositoryDao(context: Context) =
        AppDatabase.getInstance(context.applicationContext).repositoriesDao()

    override fun provideListRepositoriesFavoritesViewModelFactory(context: Context): ListRepositoriesFavoritesViewModelFactory {
        val repository = getRepository(context)
        return ListRepositoriesFavoritesViewModelFactory(
            context.applicationContext as Application,
            repository
        )
    }

}

private object Lock

@Volatile private var currentInjector: ViewModelFactoryProvider =
    DefaultViewModelProvider

class Repository2: IRepository{
    override suspend fun getRepositories(page: Int): Flow<ApiResponse<List<RepositoryItem>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRepository(
        owner: String,
        repository: String
    ): Flow<ApiResponse<RepositoryItem>> {
        TODO("Not yet implemented")
    }
}
class Local2: ILocalDataSource{}
class Remote2: IRemoteDataSource{
    override suspend fun getRepositories(page: Int): ApiResponse<RepositoryListNetwork> {
        TODO("Not yet implemented")
    }

    override suspend fun getRepository(
        owner: String,
        repository: String
    ): ApiResponse<RepositoryNetwork> {
        TODO("Not yet implemented")
    }
}

