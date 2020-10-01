package root.savaris.githubrepositories.interactors

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import root.savaris.githubrepositories.base.FlowableUseCase
import root.savaris.githubrepositories.base.LiveDataUseCase
import root.savaris.githubrepositories.data.IRepository
import root.savaris.githubrepositories.domain.RepositoryItem
import root.savaris.githubrepositories.framework.network.model.ApiResponse
import kotlin.coroutines.CoroutineContext

class IsRepositoryFavoriteUseCase (
    private val iRepository: IRepository,
    coroutineContext: CoroutineContext = Dispatchers.Main
) : LiveDataUseCase<Int, IsRepositoryFavoriteUseCase.Params>(coroutineContext) {

    override fun buildUseCaseFlowable(params: Params): LiveData<Int> {
        return iRepository.isRepositoryFavorite(params.name, params.ownerLogin)
    }

    data class Params(val name: String, val ownerLogin: String)


}