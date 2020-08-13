package root.savaris.githubrepositories.list

import android.app.Application
import androidx.lifecycle.*
import root.savaris.githubrepositories.PER_PAGE_PARAM_API
import root.savaris.githubrepositories.data.Repository
import root.savaris.githubrepositories.data.RepositoryItem
import root.savaris.githubrepositories.data.remote.rest_utils.Resource
import root.savaris.githubrepositories.data.remote.rest_utils.Status
import root.savaris.githubrepositories.utils.ListItemType.Companion.FOOTER_CONTENT

class ListRepositoriesViewModel(application: Application, val repository: Repository) :
    AndroidViewModel(application) {

    private var userLoadMore = RepositoryItem(id = FOOTER_CONTENT)
    private var listRepositories: ArrayList<RepositoryItem> = ArrayList()

    private val _shimmerVisible = MutableLiveData<Boolean>()
    val shimmerVisible: LiveData<Boolean>
        get() = _shimmerVisible

    private val _containerRecyclerViewVisible = MutableLiveData<Boolean>()
    val containerRecyclerViewVisible: LiveData<Boolean>
        get() = _containerRecyclerViewVisible

    private val _containerLayoutErrorVisible = MutableLiveData<Boolean>()
    val containerLayoutErrorVisible: LiveData<Boolean>
        get() = _containerLayoutErrorVisible

    private val _containerLayoutEmptyVisible = MutableLiveData<Boolean>()
    val containerLayoutEmptyVisible: LiveData<Boolean>
        get() = _containerLayoutEmptyVisible

    private val _refreshed = MutableLiveData<Boolean>()
    val refreshed: LiveData<Boolean>
        get() = _refreshed

    private val _removeLoadMoreCell = MutableLiveData<Boolean>()
    val removeLoadMoreCell: LiveData<Boolean>
        get() = _removeLoadMoreCell

    private val _listRepository = MutableLiveData<List<RepositoryItem>>()
    val listRepository: LiveData<List<RepositoryItem>>
        get() = _listRepository

    private val _snackMessageError = MutableLiveData<String>()
    val snackMessageError: LiveData<String>
        get() = _snackMessageError

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError


    private val _navigateToRepository = MutableLiveData<Pair<String, String>>()
    val navigateToRepository
        get() = _navigateToRepository

    val mediatorLiveDataRepositoryList = MediatorLiveData<Resource<List<RepositoryItem>>>()

    private var isRefreshing = false
    private var isLoadingMore = false

    private var hasNext: Boolean = true
    private var page = 1

    init {
        initialLoad()
    }

    fun isLoadMoreViable(): Boolean = !isRefreshing && hasNext && !isLoadingMore

    private fun loadRepositories() {
        mediatorLiveDataRepositoryList.addSource(repository.getRepositories(page, 2000L)) {
            mediatorLiveDataRepositoryList.value = it
        }
    }

    private fun showShimmer() {
        _shimmerVisible.value = true
    }

    private fun hideShimmer() {
        _shimmerVisible.value = false
    }

    fun shimmerUsed() {
        _shimmerVisible.value = null
    }

    private fun showContainerRecyclerView() {
        _containerRecyclerViewVisible.value = true
    }

    private fun hideContainerRecyclerView() {
        _containerRecyclerViewVisible.value = false
    }

    fun containerRecyclerViewUsed() {
        _containerRecyclerViewVisible.value = null
    }

    private fun showContainerLayoutError() {
        _containerLayoutErrorVisible.value = true
    }

    private fun hideContainerLayoutError() {
        _containerLayoutErrorVisible.value = false
    }

    fun containerLayoutErrorUsed() {
        _containerLayoutErrorVisible.value = null
    }

    private fun showContainerLayoutEmpty() {
        _containerLayoutEmptyVisible.value = true
    }

    private fun hideContainerLayoutEmpty() {
        _containerLayoutEmptyVisible.value = false
    }

    fun containerLayoutEmptyUsed() {
        _containerLayoutEmptyVisible.value = null
    }

    private fun loadItemsAdapter(listItems: List<RepositoryItem>) {
        _listRepository.value = listItems
    }

    fun loadItemsAdapterUsed() {
        _listRepository.value = null
    }

    private fun loadErrorMessage(message: String) {
        _messageError.value = message
    }

    fun messageErrorUsed() {
        _messageError.value = null
    }

    private fun loadSnackErrorMessage(message: String) {
        _snackMessageError.value = message
    }

    fun snackMessageErrorUsed() {
        _snackMessageError.value = null
    }

    fun refreshed() {
        _refreshed.value = true
    }

    fun refreshedUsed() {
        _refreshed.value = null
    }

    private fun removeCellFooter() {
        _removeLoadMoreCell.value = true
    }

    fun removeCellFooterUsed() {
        _removeLoadMoreCell.value = null
    }

    fun onRepositoryClicked(owner: String, repository: String) {
        _navigateToRepository.value = Pair(owner, repository)
    }

    fun onRepositoryNavigated() {
        _navigateToRepository.value = null
    }

    fun manageResponseList(resource: Resource<List<RepositoryItem>>) {
        if (resource.status == Status.SUCCESS_NETWORK && resource.data?.size ?: 0 > 0) {
            if (!isLoadingMore) {
                listRepositories.clear()
            } else {
                removeCellFooter()
            }
            if (isRefreshing) {
                hideContainerLayoutEmpty()
                hideContainerLayoutError()
            } else {
                if (!isLoadingMore) {
                    hideShimmer()
                }
            }
            verifyHasNext(resource.data!!.size)
            listRepositories.addAll(resource.data)
            loadItemsAdapter(listRepositories)
            showContainerRecyclerView()
        } else if (resource.status == Status.SUCCESS_NETWORK && resource.data?.size ?: 0 == 0) {
            if (isRefreshing) {
                hideContainerRecyclerView()
                hideContainerLayoutError()
            } else {
                hideShimmer()
            }
            showContainerLayoutEmpty()
        } else if (resource.status == Status.ERROR) {
            if (isLoadingMore && page != 1 && hasNext) {
                if (page * PER_PAGE_PARAM_API > listRepositories.size) {
                    page -= 1
                }
            }
            if (isRefreshing || isLoadingMore) {
                loadSnackErrorMessage(resource.message ?: "")
                removeCellFooter()
            } else {
                hideShimmer()
                loadErrorMessage(resource.message ?: "")
                showContainerLayoutError()
            }
        }
        if (isRefreshing) {
            refreshed()
        }
        isRefreshing = false
        isLoadingMore = false
    }

    private fun initialLoad() {
        showShimmer()
        resetParams()
        loadRepositories()
    }

    fun tryAgain() {
        showShimmer()
        hideContainerLayoutEmpty()
        hideContainerLayoutError()
        hideContainerRecyclerView()
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

}