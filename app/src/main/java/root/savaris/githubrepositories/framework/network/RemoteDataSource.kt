package root.savaris.githubrepositories.framework.network

import root.savaris.githubrepositories.data.IRemoteDataSource
import root.savaris.githubrepositories.framework.network.model.ApiResponse
import root.savaris.githubrepositories.framework.network.model.RepositoryListNetwork
import root.savaris.githubrepositories.framework.network.model.RepositoryNetwork

class RemoteDataSource(private val repositoriesServiceApi: RepositoriesServiceApi) : IRemoteDataSource {
    override suspend fun getRepositories(page: Int): ApiResponse<RepositoryListNetwork> {
        return try{
            ApiResponse.success(repositoriesServiceApi.getRepositories(page = page))
        } catch (exception: Exception) {
            ApiResponse.failure(exception)
        }
    }

    override suspend fun getRepository(
        owner: String,
        repository: String
    ): ApiResponse<RepositoryNetwork> {
        return try{
            ApiResponse.success(repositoriesServiceApi.getRepository(owner, repository))
        } catch (exception: Exception) {
            ApiResponse.failure(exception)
        }
    }
}