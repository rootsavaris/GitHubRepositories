package root.savaris.githubrepositories.favorites

import android.app.Application
import androidx.lifecycle.*
import root.savaris.githubrepositories.data.Repository
import root.savaris.githubrepositories.data.RepositoryItem

class ListRepositoriesFavoritesViewModel(application: Application, val repository: Repository) : AndroidViewModel(application) {

    val listRepositories = repository.getRepositoriesFavorites()
    private var listRepositoriesLocal: ArrayList<RepositoryItem> = ArrayList()

    private val _containerRecyclerViewVisible = MutableLiveData<Boolean>()
    val containerRecyclerViewVisible: LiveData<Boolean>
        get() = _containerRecyclerViewVisible

    private val _containerLayoutEmptyVisible = MutableLiveData<Boolean>()
    val containerLayoutEmptyVisible: LiveData<Boolean>
        get() = _containerLayoutEmptyVisible

    private val _listRepository = MutableLiveData<List<RepositoryItem>>()
    val listRepository: LiveData<List<RepositoryItem>>
        get() = _listRepository

    private val _navigateToRepository = MutableLiveData<Pair<String, String>>()
    val navigateToRepository
        get() = _navigateToRepository

    private fun showContainerRecyclerView() {
        _containerRecyclerViewVisible.value = true
    }

    private fun hideContainerRecyclerView() {
        _containerRecyclerViewVisible.value = false
    }

    fun containerRecyclerViewUsed() {
        _containerRecyclerViewVisible.value = null
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

    fun onRepositoryClicked(owner: String, repository: String) {
        _navigateToRepository.value = Pair(owner, repository)
    }

    fun onRepositoryNavigated() {
        _navigateToRepository.value = null
    }

    fun manageResponseList(list: List<RepositoryItem>?) {
        if (list == null || list.isEmpty()){
            hideContainerRecyclerView()
            showContainerLayoutEmpty()
        } else {
            hideContainerLayoutEmpty()
            listRepositoriesLocal.clear()
            listRepositoriesLocal.addAll(list)
            loadItemsAdapter(listRepositoriesLocal)
            showContainerRecyclerView()
        }
    }

    fun removeFavorite(repositoryItem: RepositoryItem) {
        repository.removeFavorite(repositoryItem)
    }


}