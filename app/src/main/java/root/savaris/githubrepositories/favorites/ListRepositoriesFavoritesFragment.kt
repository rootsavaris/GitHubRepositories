package root.savaris.githubrepositories.favorites

import android.app.Application
import android.os.Bundle
import android.view.*
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import root.savaris.githubrepositories.R
import root.savaris.githubrepositories.data.Repository
import root.savaris.githubrepositories.databinding.FragmentListFavoritesBinding
import root.savaris.githubrepositories.utils.Injector
import root.savaris.githubrepositories.utils.RepositoryClickListener

class ListRepositoriesFavoritesFragment : Fragment() {

    private lateinit var binding: FragmentListFavoritesBinding
    private var listRepositoriesFavoritesAdapter: ListRepositoriesFavoritesAdapter? = null

    private val viewModel: ListRepositoriesFavoritesViewModel by viewModels {
        Injector.provideListRepositoriesFavoritesViewModelFactory(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentListFavoritesBinding>(inflater,
            R.layout.fragment_list_favorites, container,false)

        activity?.let {

            loadViews()

            viewModel.containerRecyclerViewVisible.observe(viewLifecycleOwner, Observer { visible ->
                visible?.let {
                    binding.containerRecyclerView.root.visibility = if (visible) View.VISIBLE else View.GONE
                    viewModel.containerRecyclerViewUsed()
                }
            })

            viewModel.containerLayoutEmptyVisible.observe(viewLifecycleOwner, Observer { visible ->
                visible?.let {
                    binding.containerEmptyList.root.visibility = if (visible) View.VISIBLE else View.GONE
                    viewModel.containerLayoutEmptyUsed()
                }
            })

            viewModel.listRepository.observe(viewLifecycleOwner, Observer { listItem ->
                listItem?.let {
                    listRepositoriesFavoritesAdapter?.submitNewList(listItem)
                    viewModel.loadItemsAdapterUsed()
                }
            })

            viewModel.navigateToRepository.observe(this, Observer { pairRepository ->
                pairRepository?.let {
                    findNavController().navigate(ListRepositoriesFavoritesFragmentDirections.actionListRepositoriesFavoritesFragmentToDetailRepositoryFragment(pairRepository.first, pairRepository.second))
                    viewModel.onRepositoryNavigated()
                }
            })

            viewModel.listRepositories.observe(this, Observer { repositories ->
                viewModel.manageResponseList(repositories)
            })

        }

        return binding.root

    }

    private fun loadViews() {

        with(binding.containerRecyclerView.commonRecyclerView){
            layoutManager = LinearLayoutManager(activity).also { layoutManager -> layoutManager.findLastCompletelyVisibleItemPosition() }
            listRepositoriesFavoritesAdapter = ListRepositoriesFavoritesAdapter(activity!!, RepositoryClickListener { repositoryOwner, repositoryName ->
                viewModel.onRepositoryClicked(repositoryOwner, repositoryName)
            }, RepositoryRemoveClickListener { repositoryItem ->
                viewModel.removeFavorite(repositoryItem)
            })
            adapter = listRepositoriesFavoritesAdapter
            ViewCompat.setNestedScrollingEnabled(this, true)
        }

    }

}

class ListRepositoriesFavoritesViewModelFactory(
    private val application: Application,
    private val repository: Repository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = ListRepositoriesFavoritesViewModel(application, repository) as T
}