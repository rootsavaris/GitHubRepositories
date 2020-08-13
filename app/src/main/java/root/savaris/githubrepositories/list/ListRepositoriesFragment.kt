package root.savaris.githubrepositories.list

import android.app.Application
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import root.savaris.githubrepositories.R
import root.savaris.githubrepositories.data.Repository
import root.savaris.githubrepositories.databinding.FragmentListRepositoriesBinding
import root.savaris.githubrepositories.utils.Injector
import root.savaris.githubrepositories.utils.RepositoryClickListener

class ListRepositoriesFragment : Fragment() {

    private lateinit var binding: FragmentListRepositoriesBinding
    private var listRepositoriesAdapter: ListRepositoriersAdapter? = null

    private val viewModel: ListRepositoriesViewModel by viewModels {
        Injector.provideListRepositoriesViewModelFactory(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentListRepositoriesBinding>(inflater,
            R.layout.fragment_list_repositories,container,false)

        activity?.let {
            loadViews()

            viewModel.shimmerVisible.observe(viewLifecycleOwner, Observer { shimmer ->
                shimmer?.let {
                    if (it){
                        binding.shimmerRecycleContainer.startShimmer()
                        binding.shimmerRecycleContainer.visibility = View.VISIBLE
                    } else {
                        binding.shimmerRecycleContainer.stopShimmer()
                        binding.shimmerRecycleContainer.visibility = View.GONE
                    }
                    viewModel.shimmerUsed()
                }
            })

            viewModel.containerRecyclerViewVisible.observe(viewLifecycleOwner, Observer { visible ->
                visible?.let {
                    binding.containerRecyclerView.root.visibility = if (visible) View.VISIBLE else View.GONE
                    viewModel.containerRecyclerViewUsed()
                }
            })

            viewModel.containerLayoutErrorVisible.observe(viewLifecycleOwner, Observer { visible ->
                visible?.let {
                    binding.containerError.root.visibility = if (visible) View.VISIBLE else View.GONE
                    viewModel.containerLayoutErrorUsed()
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
                    listRepositoriesAdapter?.submitNewList(listItem)
                    viewModel.loadItemsAdapterUsed()
                }
            })

            viewModel.messageError.observe(viewLifecycleOwner, Observer { message ->
                message?.let {
                    binding.containerError.txtErrorMsg.text = message
                    viewModel.messageErrorUsed()
                }
            })

            viewModel.snackMessageError.observe(viewLifecycleOwner, Observer { message ->
                message?.let {
                    showSnackError(message)
                    viewModel.snackMessageErrorUsed()
                }
            })

            viewModel.mediatorLiveDataRepositoryList.observe(viewLifecycleOwner, Observer { listResource ->
                viewModel.manageResponseList(listResource)
            })

            viewModel.refreshed.observe(viewLifecycleOwner, Observer {refreshed ->
                refreshed?.let{
                    if (binding.swipeUserTimeline.isRefreshing) {
                        binding.swipeUserTimeline.isRefreshing = false
                    }
                    viewModel.refreshedUsed()
                }
            })

            viewModel.removeLoadMoreCell.observe(viewLifecycleOwner, Observer {removed ->
                removed?.let{
                    removeFooter()
                    viewModel.removeCellFooterUsed()
                }
            })

            viewModel.navigateToRepository.observe(viewLifecycleOwner, Observer {repositoryId ->
                repositoryId?.let{
                    removeFooter()
                    viewModel.removeCellFooterUsed()
                }
            })

            viewModel.navigateToRepository.observe(this, Observer { pairRepository ->
                pairRepository?.let {
                    findNavController().navigate(ListRepositoriesFragmentDirections.actionListRepositoriesFragmentToDetailRepositoryFragment(pairRepository.first, pairRepository.second))
                    viewModel.onRepositoryNavigated()
                }
            })

        }

        return binding.root

    }

    private fun loadViews() {

        binding.containerError.txtErrorTitle.text = getString(R.string.something_went_wrong)
        with(binding.containerError.btnTryAgain){
            text = getString(R.string.alertTryAgain)
            setOnClickListener {
                viewModel.tryAgain()
            }
        }

        with(binding.containerRecyclerView.commonRecyclerView){
            layoutManager = LinearLayoutManager(activity).also { layoutManager -> layoutManager.findLastCompletelyVisibleItemPosition() }
            listRepositoriesAdapter = ListRepositoriersAdapter(activity!!, RepositoryClickListener { repositoryOwner, repositoryName ->
                viewModel.onRepositoryClicked(repositoryOwner, repositoryName)
            })
            adapter = listRepositoriesAdapter
            ViewCompat.setNestedScrollingEnabled(this, true)
        }

        binding.userNestedScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (v?.getChildAt(v.childCount - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight)) &&
                    scrollY > oldScrollY) {
                    if (binding.containerRecyclerView.commonRecyclerView.layoutManager != null && listRepositoriesAdapter?.isLoadingEnabled() == false) {
                        val visibleItemCount = binding.containerRecyclerView.commonRecyclerView.layoutManager!!.childCount
                        val totalItemCount = binding.containerRecyclerView.commonRecyclerView.layoutManager!!.itemCount
                        val pastVisiblesItems = (binding.containerRecyclerView.commonRecyclerView.layoutManager!! as LinearLayoutManager).findFirstVisibleItemPosition()
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount && viewModel.isLoadMoreViable()) {
                            addFooter()
                        }
                    }
                }
            }
        })

        binding.swipeUserTimeline.setOnRefreshListener {
            viewModel.refreshList()
        }

    }

    private fun showSnackError(messageError: String) {
        activity?.let {
            Snackbar.make(binding.root, getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG)
                .setTextColor(ContextCompat.getColor(it, android.R.color.white))
                .setAction(getString(R.string.seeMore)) { showErroDialog(messageError) }
                .setActionTextColor(ContextCompat.getColor(it, android.R.color.white))
                .show()
        }
    }

    private fun showErroDialog(messageError: String) {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle(R.string.something_went_wrong)
                setMessage(messageError)
                setPositiveButton(android.R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            }
            builder.create()
        }
        alertDialog?.show()
    }

    private fun addFooter() {
        viewModel.addLoadMoreItem()
    }

    private fun removeFooter() {
        viewModel.removeLoadMoreItem()
    }

}

class ListRepositoriesViewModelFactory(
    private val application: Application,
    private val repository: Repository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = ListRepositoriesViewModel(application, repository) as T
}


