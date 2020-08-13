package root.savaris.githubrepositories.detail

import android.app.Application
import androidx.lifecycle.*
import root.savaris.githubrepositories.data.Repository
import root.savaris.githubrepositories.data.RepositoryItem
import root.savaris.githubrepositories.data.remote.rest_utils.Resource
import root.savaris.githubrepositories.data.remote.rest_utils.Status

class DetailRepositoryViewModel(application: Application, val repository: Repository, val owner: String, private val repositoryName: String) :
    AndroidViewModel(application) {

    private lateinit var repositoryItem: RepositoryItem

    private val _shimmerVisible = MutableLiveData<Boolean>()
    val shimmerVisible: LiveData<Boolean>
        get() = _shimmerVisible

    private val _containerItemVisible = MutableLiveData<Boolean>()
    val containerItemVisible: LiveData<Boolean>
        get() = _containerItemVisible

    private val _containerLayoutErrorVisible = MutableLiveData<Boolean>()
    val containerLayoutErrorVisible: LiveData<Boolean>
        get() = _containerLayoutErrorVisible

    private val _containerLayoutEmptyVisible = MutableLiveData<Boolean>()
    val containerLayoutEmptyVisible: LiveData<Boolean>
        get() = _containerLayoutEmptyVisible

    private val _refreshed = MutableLiveData<Boolean>()
    val refreshed: LiveData<Boolean>
        get() = _refreshed

    private val _itemRepository = MutableLiveData<RepositoryItem>()
    val itemRepository: LiveData<RepositoryItem>
        get() = _itemRepository

    val itemFavorited: LiveData<Int> = repository.isRepositoryFavorite(repositoryName, owner)

    private val _snackMessageError = MutableLiveData<String>()
    val snackMessageError: LiveData<String>
        get() = _snackMessageError

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    val mediatorLiveDataRepository = MediatorLiveData<Resource<RepositoryItem>>()

    private var isRefreshing = false

    init {
        initialLoad()
    }

    private fun loadRepository() {
        mediatorLiveDataRepository.addSource(repository.getRepository(owner, repositoryName, 2000L)) {
            mediatorLiveDataRepository.value = it
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

    private fun showContainerItem() {
        _containerItemVisible.value = true
    }

    private fun hideContainerItem() {
        _containerItemVisible.value = false
    }

    fun containerItemUsed() {
        _containerItemVisible.value = null
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

    private fun loadItem(item: RepositoryItem) {
        _itemRepository.value = item
    }

    fun loadItemUsed() {
        _itemRepository.value = null
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

    fun manageResponse(resource: Resource<RepositoryItem>) {
        if (resource.status == Status.SUCCESS_NETWORK && resource.data != null) {
            if (isRefreshing) {
                hideContainerLayoutEmpty()
                hideContainerLayoutError()
            } else {
                hideShimmer()
            }
            repositoryItem = resource.data
            loadItem(resource.data)
            showContainerItem()
        } else if (resource.status == Status.SUCCESS_NETWORK && resource.data == null) {
            if (isRefreshing) {
                hideContainerItem()
                hideContainerLayoutError()
            } else {
                hideShimmer()
            }
            showContainerLayoutEmpty()
        } else if (resource.status == Status.ERROR) {
            if (isRefreshing) {
                loadSnackErrorMessage(resource.message ?: "")
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
    }

    private fun initialLoad() {
        showShimmer()
        loadRepository()
    }

    fun tryAgain() {
        showShimmer()
        hideContainerLayoutEmpty()
        hideContainerLayoutError()
        hideContainerItem()
        loadRepository()
    }

    fun refreshList() {
        isRefreshing = true
        loadRepository()
    }

    fun favoriteItem() {
        repository.insertFavorite(repositoryItem)
    }

    fun removeFavoriteItem() {
        repository.removeFavorite(repositoryItem)
    }

}