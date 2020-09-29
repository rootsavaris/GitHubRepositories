package root.savaris.githubrepositories.presentation.list

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import root.savaris.githubrepositories.R
import root.savaris.githubrepositories.databinding.FragmentListRepositoriesBinding
import org.koin.android.viewmodel.ext.android.viewModel
import root.savaris.githubrepositories.utils.RepositoryClickListener

class ListRepositoriesFragment : Fragment() {

    private lateinit var binding: FragmentListRepositoriesBinding
    private var listRepositoriesAdapter: ListRepositoriersAdapter? = null
    private val viewModel: ListRepositoriesViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentListRepositoriesBinding>(inflater,
            R.layout.fragment_list_repositories,container,false)

        activity?.let {

            loadViews()

            viewModel.listRepository.observe(viewLifecycleOwner, Observer { listItem ->
                listItem?.let {
                    listRepositoriesAdapter?.submitNewList(listItem)
                }
            })

            viewModel.state.observe(this, Observer { states ->
                states?.let {

                    if (it.isLoading){
                        binding.shimmerRecycleContainer.startShimmer()
                        binding.shimmerRecycleContainer.visibility = View.VISIBLE
                    } else {
                        binding.shimmerRecycleContainer.stopShimmer()
                        binding.shimmerRecycleContainer.visibility = View.GONE
                    }

                    binding.containerRecyclerView.root.visibility = if (it.containerRecyclerVisible) View.VISIBLE else View.GONE
                    binding.containerEmptyList.root.visibility = if (it.containerEmptyLayoutVisible) View.VISIBLE else View.GONE
                    binding.containerError.root.visibility = if (it.containerErrorLayoutVisible) View.VISIBLE else View.GONE

                    if (it.removeFooter){
                        removeFooter()
                    }

                    if (binding.swipeUserTimeline.isRefreshing) {
                        binding.swipeUserTimeline.isRefreshing = false
                    }

                    if (!it.messageError.isBlank()){
                        binding.containerError.txtErrorMsg.text = it.messageError
                    }

                    if (it.showSnackBar) {
                        showSnackError(it.messageError)
                    } else if (it.containerErrorLayoutVisible && !it.showSnackBar) {
                        binding.containerError.txtErrorMsg.text = it.messageError
                    }

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
            listRepositoriesAdapter = ListRepositoriersAdapter( RepositoryClickListener { repositoryOwner, repositoryName ->
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



