package root.savaris.githubrepositories.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import root.savaris.githubrepositories.data.local.RepositoriesDao
import root.savaris.githubrepositories.data.local.fromDomain
import root.savaris.githubrepositories.data.local.toDomain
import root.savaris.githubrepositories.data.remote.RemoteRepositoryDatasource
import root.savaris.githubrepositories.data.remote.model.RepositoryList
import root.savaris.githubrepositories.data.remote.model.RepositoryRemoteItemEntity
import root.savaris.githubrepositories.data.remote.rest_utils.ApiResponse
import root.savaris.githubrepositories.data.remote.rest_utils.AppExecutors
import root.savaris.githubrepositories.data.remote.rest_utils.NetworkBoundResourceOnly
import root.savaris.githubrepositories.data.remote.rest_utils.Resource
import root.savaris.githubrepositories.data.remote.toDomain

class Repository (private val context: Context,
                  private val repositoryDao: RepositoriesDao,
                  private val appExecutors: AppExecutors,
                  private val remoteRepository: RemoteRepositoryDatasource) {

    fun getRepositories(page: Int, callDelay: Long = 0): LiveData<Resource<List<RepositoryItem>>> {
        return object : NetworkBoundResourceOnly<List<RepositoryItem>, RepositoryList>(appExecutors) {
            override fun fetchDelayMillis(): Long {
                return callDelay
            }
            override fun createCall(): LiveData<ApiResponse<RepositoryList>> = remoteRepository.getRepositories(page)
            override fun transFormCallResult(item: RepositoryList): List<RepositoryItem>? {
                return item.items.map { it.toDomain() }
            }
        }.asLiveData()
    }

    fun getRepository(owner: String, repository: String, callDelay: Long = 0): LiveData<Resource<RepositoryItem>> {
        return object : NetworkBoundResourceOnly<RepositoryItem, RepositoryRemoteItemEntity>(appExecutors) {
            override fun fetchDelayMillis(): Long {
                return callDelay
            }
            override fun createCall(): LiveData<ApiResponse<RepositoryRemoteItemEntity>> = remoteRepository.getRepository(owner, repository)
            override fun transFormCallResult(item: RepositoryRemoteItemEntity): RepositoryItem? {
                return item.toDomain()
            }
        }.asLiveData()
    }

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

}
