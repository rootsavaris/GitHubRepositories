package root.savaris.githubrepositories.data

import root.savaris.githubrepositories.framework.network.model.ApiResponse
import root.savaris.githubrepositories.framework.network.model.RepositoryListNetwork
import root.savaris.githubrepositories.framework.network.model.RepositoryNetwork

interface IRemoteDataSource {
    suspend fun getRepositories(page: Int): ApiResponse<RepositoryListNetwork>
    suspend fun getRepository(owner: String, repository: String): ApiResponse<RepositoryNetwork>
}