package root.savaris.githubrepositories.data.remote

import androidx.lifecycle.LiveData
import root.savaris.githubrepositories.framework.network.model.RepositoryNetwork
import root.savaris.githubrepositories.framework.network.model.RepositoryListNetwork
import root.savaris.githubrepositories.framework.network.model.ApiResponse

interface RemoteRepositoryDatasource {
    fun getRepositories(page: Int): LiveData<ApiResponse<RepositoryListNetwork>>
    fun getRepository(owner: String, repository: String): LiveData<ApiResponse<RepositoryNetwork>>
}