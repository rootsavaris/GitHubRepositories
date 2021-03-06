package root.savaris.githubrepositories.data.remote

import androidx.lifecycle.LiveData
import root.savaris.githubrepositories.data.remote.model.RepositoryRemoteItemEntity
import root.savaris.githubrepositories.data.remote.model.RepositoryList
import root.savaris.githubrepositories.data.remote.rest_utils.ApiResponse

interface RemoteRepositoryDatasource {
    fun getRepositories(page: Int): LiveData<ApiResponse<RepositoryList>>
    fun getRepository(owner: String, repository: String): LiveData<ApiResponse<RepositoryRemoteItemEntity>>
}