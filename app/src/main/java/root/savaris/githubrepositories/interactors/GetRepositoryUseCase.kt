package root.savaris.githubrepositories.interactors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import root.savaris.githubrepositories.base.FlowableUseCase
import root.savaris.githubrepositories.data.IRepository
import root.savaris.githubrepositories.framework.network.model.ApiResponse
import root.savaris.githubrepositories.domain.RepositoryItem
import kotlin.coroutines.CoroutineContext

class GetRepositoryUseCase(
    private val iRepository: IRepository,
    coroutineContext: CoroutineContext = Dispatchers.Main
) : FlowableUseCase<ApiResponse<RepositoryItem>, GetRepositoryUseCase.Params>(coroutineContext) {

    override suspend fun buildUseCaseFlowable(params: Params): Flow<ApiResponse<RepositoryItem>> {
        return iRepository.getRepository(params.owner, params.repository)
    }

    data class Params(val owner: String, val repository: String)

}