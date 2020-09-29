package root.savaris.githubrepositories.interactors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import root.savaris.githubrepositories.base.FlowableUseCase
import root.savaris.githubrepositories.data.IRepository
import root.savaris.githubrepositories.framework.network.model.ApiResponse
import root.savaris.githubrepositories.domain.RepositoryItem
import kotlin.coroutines.CoroutineContext

class GetRepositoriesUseCase(
    private val iRepository: IRepository,
    coroutineContext: CoroutineContext = Dispatchers.Main
) : FlowableUseCase<ApiResponse<List<RepositoryItem>>, GetRepositoriesUseCase.Params>(coroutineContext) {

    override suspend fun buildUseCaseFlowable(params: Params): Flow<ApiResponse<List<RepositoryItem>>> {
        return iRepository.getRepositories(params.page)
    }

    data class Params(val page: Int)

}