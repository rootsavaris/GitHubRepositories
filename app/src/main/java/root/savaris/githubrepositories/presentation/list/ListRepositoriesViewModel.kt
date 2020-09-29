package root.savaris.githubrepositories.presentation.list

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import root.savaris.githubrepositories.PER_PAGE_PARAM_API
import root.savaris.githubrepositories.base.BaseAction
import root.savaris.githubrepositories.base.BaseViewModel
import root.savaris.githubrepositories.base.BaseViewState
import root.savaris.githubrepositories.domain.RepositoryItem
import root.savaris.githubrepositories.framework.network.model.ApiResponse
import root.savaris.githubrepositories.interactors.GetRepositoriesUseCase
import root.savaris.githubrepositories.utils.ListItemType.Companion.FOOTER_CONTENT

class ListRepositoriesViewModel(application: Application, private val getRepositoriesUseCase: GetRepositoriesUseCase) :
    BaseViewModel<ListRepositoriesViewModel.ViewState, ListRepositoriesViewModel.Action>(application, ViewState()) {

    private var userLoadMore =
        RepositoryItem(id = FOOTER_CONTENT)

    private var listRepositories: ArrayList<RepositoryItem> = ArrayList()

    private val _listRepository = MutableLiveData<List<RepositoryItem>>()
    val listRepository: LiveData<List<RepositoryItem>>
        get() = _listRepository

    private val _navigateToRepository = MutableLiveData<Pair<String, String>>()
    val navigateToRepository
        get() = _navigateToRepository

    private var isRefreshing = false
    private var isLoadingMore = false

    private var hasNext: Boolean = true
    private var page = 1

    init {
        resetParams()
        initialLoad()
    }

    fun isLoadMoreViable(): Boolean = !isRefreshing && hasNext && !isLoadingMore

    private fun loadRepositories() {
        initialLoad()
    }

    private fun loadItemsAdapter(listItems: List<RepositoryItem>) {
        _listRepository.value = listItems
    }

    fun onRepositoryClicked(owner: String, repository: String) {
        _navigateToRepository.value = Pair(owner, repository)
    }

    fun onRepositoryNavigated() {
        _navigateToRepository.value = null
    }

    private fun initialLoad() {
        viewModelScope.launch {
            getRepositoriesUseCase.execute(GetRepositoriesUseCase.Params(page),
                {
                    viewModelScope.launch {
                        withContext(Dispatchers.Main){
                            if (!isRefreshing && !isLoadingMore){
                                sendAction(Action.ShowInitialState)
                            }
                        }
                    }
                },
                {

                    if (it is ApiResponse.Success){
                        if (!it.data.isNullOrEmpty()){
                            if (!isLoadingMore) {
                                listRepositories.clear()
                            }
                            verifyHasNext(it.data.size)
                            listRepositories.addAll(it.data)
                            loadItemsAdapter(listRepositories)
                            sendAction(Action.ShowListSuccess(isLoadingMore))
                        } else {
                            sendAction(Action.ShowListEmpty)
                        }
                    } else if (it is ApiResponse.Failure){

                        if (isLoadingMore && page != 1 && hasNext){
                            if (page * PER_PAGE_PARAM_API > listRepositories.size){
                                page -= 1
                            }
                        }

                        val messageError = it.e.message ?: ""

                        if (listRepositories.isEmpty()){
                            sendAction(Action.ShowListFail(messageError))
                        } else {
                            sendAction(Action.ShowListFailWithSnackBar(isLoadingMore, messageError))
                        }
                    }

                }, {}
            )
            isRefreshing = false
            isLoadingMore = false
        }
    }

    fun tryAgain() {
        loadRepositories()
    }

    fun refreshList() {
        resetParams()
        isRefreshing = true
        loadRepositories()
    }

    private fun resetParams() {
        hasNext = false
        page = 1
    }

    private fun verifyHasNext(responseListSize: Int) {
        if (responseListSize == PER_PAGE_PARAM_API) {
            hasNext = true
        }
    }

    fun addLoadMoreItem() {
        listRepositories.add(userLoadMore)
        loadItemsAdapter(listRepositories)
        page += 1
        isLoadingMore = true
        loadRepositories()
    }

    fun removeLoadMoreItem(): Boolean {
        if (listRepositories.size > 0) {
            listRepositories.remove(userLoadMore)
            loadItemsAdapter(listRepositories)
            return true
        }
        return false
    }


    override fun onReduceState(viewAction: Action) = when (viewAction) {
        is Action.ShowInitialState -> ViewState(
            isLoading = true
        )
        is Action.ShowListSuccess -> ViewState(
            isLoading = false,
            containerRecyclerVisible = true,
            removeFooter = viewAction.removeFooter
        )
        is Action.ShowListEmpty -> ViewState(
            isLoading = false,
            containerEmptyLayoutVisible = true
        )
        is Action.ShowListFail -> ViewState(
            isLoading = false,
            containerErrorLayoutVisible = true,
            messageError = viewAction.messageError
        )
        is Action.ShowListFailWithSnackBar -> ViewState(
            isLoading = false,
            containerRecyclerVisible = true,
            removeFooter = viewAction.removeFooter,
            messageError = viewAction.messageError,
            showSnackBar = true
        )

    }

    data class ViewState(val isLoading: Boolean = true,
                         val containerRecyclerVisible: Boolean = false,
                         val containerEmptyLayoutVisible: Boolean = false,
                         val containerErrorLayoutVisible: Boolean = false,
                         val removeFooter: Boolean = false,
                         val messageError: String = "",
                         val showSnackBar: Boolean = false
    ) : BaseViewState

    sealed class Action : BaseAction {
        object ShowInitialState : Action()
        class ShowListSuccess(val removeFooter: Boolean = false) : Action()
        object ShowListEmpty : Action()
        class ShowListFail(val messageError: String) : Action()
        class ShowListFailWithSnackBar(val removeFooter: Boolean = false, val messageError: String) : Action()
    }

}