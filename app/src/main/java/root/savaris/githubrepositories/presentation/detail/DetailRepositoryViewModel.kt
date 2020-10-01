package root.savaris.githubrepositories.presentation.detail

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import root.savaris.githubrepositories.base.BaseAction
import root.savaris.githubrepositories.base.BaseViewModel
import root.savaris.githubrepositories.base.BaseViewState
import root.savaris.githubrepositories.domain.RepositoryItem
import root.savaris.githubrepositories.framework.network.model.ApiResponse
import root.savaris.githubrepositories.interactors.GetRepositoryUseCase
import root.savaris.githubrepositories.interactors.IsRepositoryFavoriteUseCase

class DetailRepositoryViewModel(
    application: Application,
    private val owner: String,
    private val repository: String,
    private val getRepositoryUseCase: GetRepositoryUseCase,
    private val isRepositoryFavoriteUseCase: IsRepositoryFavoriteUseCase
) : BaseViewModel<DetailRepositoryViewModel.ViewState, DetailRepositoryViewModel.Action>(application, ViewState()) {

    internal var repositoryItem: RepositoryItem? = null

//    val itemFavorited: LiveData<Int> = repository.isRepositoryFavorite(repositoryName, owner)
    val itemFavorited: LiveData<Int> = isRepositoryFavoriteUseCase.execute(IsRepositoryFavoriteUseCase.Params(repository, owner))

    private var isRefreshing = false

    init {
        initialLoad()
    }

    private fun loadRepository() {
        viewModelScope.launch {
            getRepositoryUseCase.execute(GetRepositoryUseCase.Params(owner, repository),
                {
                    viewModelScope.launch {
                        withContext(Dispatchers.Main){
                            if (!isRefreshing){
                                sendAction(Action.ShowInitialState)
                            }
                        }
                    }
                },
                {
                    if (it is ApiResponse.Success){
                        repositoryItem = it.data
                        sendAction(Action.ShowSuccess)
                    } else if (it is ApiResponse.Failure){
                        val messageError = it.e.message ?: ""
                        if(repositoryItem == null){
                            sendAction(Action.ShowFail(messageError))
                        } else {
                            sendAction(Action.ShowFailWithSnackBar(messageError))
                        }
                    }

                }, {}
            )
            isRefreshing = false
        }

    }

    private fun initialLoad() {
        loadRepository()
    }

    fun tryAgain() {
        loadRepository()
    }

    fun refreshList() {
        isRefreshing = true
        loadRepository()
    }

    fun favoriteItem() {
//        repository.insertFavorite(repositoryItem)
    }

    fun removeFavoriteItem() {
//        repository.removeFavorite(repositoryItem)
    }

    override fun onReduceState(viewAction: Action) = when (viewAction) {
        is Action.ShowInitialState -> ViewState(
            isLoading = true
        )
        is Action.ShowSuccess -> ViewState(
            isLoading = false,
            containerItemVisible = true,
            updateLayoutRepository = true
        )
        is Action.ShowFail -> ViewState(
            isLoading = false,
            containerErrorLayoutVisible = true,
            messageError = viewAction.messageError
        )
        is Action.ShowFailWithSnackBar -> ViewState(
            isLoading = false,
            containerItemVisible = true,
            messageError = viewAction.messageError,
            showSnackBar = true
        )

    }

    data class ViewState(
        val isLoading: Boolean = true,
        val containerItemVisible: Boolean = false,
        val containerEmptyLayoutVisible: Boolean = false,
        val containerErrorLayoutVisible: Boolean = false,
        val messageError: String = "",
        val showSnackBar: Boolean = false,
        val updateLayoutRepository: Boolean = false
    ) : BaseViewState

    sealed class Action : BaseAction {
        object ShowInitialState : Action()
        object ShowSuccess : Action()
        class ShowFail(val messageError: String) : Action()
        class ShowFailWithSnackBar(
            val messageError: String
        ) : Action()
    }
}