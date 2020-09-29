package root.savaris.githubrepositories.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import root.savaris.githubrepositories.framework.network.model.ApiResponse
import root.savaris.githubrepositories.domain.RepositoryItem
import root.savaris.githubrepositories.framework.network.toDomain

class Repository (private val iLocalDataSource: ILocalDataSource, private val iRemoteDataSource: IRemoteDataSource) : IRepository {

    override suspend fun getRepositories(page: Int): Flow<ApiResponse<List<RepositoryItem>>> {
        return flow{
            val apiResponse = iRemoteDataSource.getRepositories(page)
            if (apiResponse is ApiResponse.Success){
                val list = apiResponse.data.items.map { it.toDomain() }
                emit(ApiResponse.success(list))
            } else if (apiResponse is ApiResponse.Failure){
                emit(ApiResponse.failure(apiResponse.e))
            }
        }
    }

    override suspend fun getRepository(
        owner: String,
        repository: String
    ): Flow<ApiResponse<RepositoryItem>> {
        return flow{
            val apiResponse = iRemoteDataSource.getRepository(owner, repository)
            if (apiResponse is ApiResponse.Success){
                emit(ApiResponse.success(apiResponse.data.toDomain()))
            } else if (apiResponse is ApiResponse.Failure){
                emit(ApiResponse.failure(apiResponse.e))
            }
        }
    }

    /*
    fun insertFavorite(repositoryItem: RepositoryItem){
        appExecutors.diskIO().execute {
            repositoryDao.insert(repositoryItem.fromDomain())
        }
    }

    fun removeFavorite(repositoryItem: RepositoryItem){
        appExecutors.diskIO().execute {
            repositoryDao.delete(repositoryItem.fromDomain())
        }
    }

    fun getRepositoriesFavorites(): LiveData<List<RepositoryItem>>{
        return repositoryDao.getRepositories().map { entitiesList -> entitiesList.map { it.toDomain() } }
    }

    fun isRepositoryFavorite(name: String, ownerLogin: String): LiveData<Int> {
        return repositoryDao.isRepositoryFavorite(name, ownerLogin)
    }

    companion object {
        @Volatile private var instance: Repository? = null
        fun getInstance(context: Context,
                        repositoryDao: RepositoriesDao,
                        appExecutors: AppExecutors, remoteRepository: RemoteRepositoryDatasource) =
            instance ?: synchronized(this) {
                instance ?: Repository(context, repositoryDao, appExecutors, remoteRepository).also { instance = it }
            }
    }
    */

}
