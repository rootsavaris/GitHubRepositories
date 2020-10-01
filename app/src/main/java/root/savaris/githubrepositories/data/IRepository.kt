package root.savaris.githubrepositories.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import root.savaris.githubrepositories.framework.network.model.ApiResponse
import root.savaris.githubrepositories.domain.RepositoryItem

interface IRepository {
    suspend fun getRepositories(page: Int): Flow<ApiResponse<List<RepositoryItem>>>
    suspend fun getRepository(owner: String, repository: String): Flow<ApiResponse<RepositoryItem>>
    fun isRepositoryFavorite(name: String, ownerLogin: String): LiveData<Int>
}