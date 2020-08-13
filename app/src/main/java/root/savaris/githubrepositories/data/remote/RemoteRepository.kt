package root.savaris.githubrepositories.data.remote

import androidx.lifecycle.LiveData
import root.savaris.githubrepositories.data.remote.model.RepositoryRemoteItemEntity
import root.savaris.githubrepositories.data.remote.model.RepositoryList
import root.savaris.githubrepositories.data.remote.rest_utils.ApiResponse

class RemoteRepository(private val networkService: NetworkService): RemoteRepositoryDatasource {

    override fun getRepositories(page: Int): LiveData<ApiResponse<RepositoryList>> {
        return networkService.getRepositories(page)
    }

    override fun getRepository(
        owner: String,
        repository: String
    ): LiveData<ApiResponse<RepositoryRemoteItemEntity>> {
        return networkService.getRepository(owner, repository)
    }
}