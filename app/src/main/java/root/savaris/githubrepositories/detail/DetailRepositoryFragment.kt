package root.savaris.githubrepositories.detail

import android.app.Application
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.layout_repository_detail.view.*
import root.savaris.githubrepositories.R
import root.savaris.githubrepositories.data.Repository
import root.savaris.githubrepositories.databinding.FragmentDetailRepositoryBinding
import root.savaris.githubrepositories.utils.Injector

class DetailRepositoryFragment : Fragment() {

    private lateinit var binding: FragmentDetailRepositoryBinding

    private val viewModel: DetailRepositoryViewModel by viewModels {
        Injector.provideDetailRepositoryViewModelFactory(requireContext(), DetailRepositoryFragmentArgs.fromBundle(arguments!!).owner, DetailRepositoryFragmentArgs.fromBundle(arguments!!).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate<FragmentDetailRepositoryBinding>(inflater,
            R.layout.fragment_detail_repository,container,false)

        activity?.let {

            loadViews()

            viewModel.shimmerVisible.observe(viewLifecycleOwner, Observer { shimmer ->
                shimmer?.let {
                    if (it){
                        binding.shimmerContainer.startShimmer()
                        binding.shimmerContainer.visibility = View.VISIBLE
                    } else {
                        binding.shimmerContainer.stopShimmer()
                        binding.shimmerContainer.visibility = View.GONE
                    }
                    viewModel.shimmerUsed()
                }
            })

            viewModel.containerItemVisible.observe(viewLifecycleOwner, Observer { visible ->
                visible?.let {
                    binding.containerItem.root.visibility = if (visible) View.VISIBLE else View.GONE
                    viewModel.containerItemUsed()
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
                    binding.containerEmpty.root.visibility = if (visible) View.VISIBLE else View.GONE
                    viewModel.containerLayoutEmptyUsed()
                }
            })

            viewModel.itemRepository.observe(viewLifecycleOwner, Observer { item ->
                item?.let {
                    with(binding.containerItem.containerItem){
                        this.txtLogin.text = item.name
                        this.txtDescription.text = item.description
                        this.txtNumberForks.text = item.forks_count.toString()
                        this.txtNumberRatings.text = item.stargazersCount.toString()
                        this.txtNumberSubscribes.text = item.subscribers_count.toString()
                        this.txtNumberWatchers.text = item.watchersCount.toString()

                        Glide.with(this@DetailRepositoryFragment)
                            .load(item.ownerAvatar)
                            .into(this.imgAvatar)

                    }
                    viewModel.loadItemUsed()
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

            viewModel.itemFavorited.observe(viewLifecycleOwner, Observer { count ->
                count?.let {
                    if (count > 0){
                        binding.containerItem.imgFavorite.setColorFilter(ContextCompat.getColor(activity!!, android.R.color.holo_orange_light))
                    } else {
                        binding.containerItem.imgFavorite.setColorFilter(ContextCompat.getColor(activity!!, android.R.color.darker_gray))
                    }
                }
            })

            viewModel.mediatorLiveDataRepository.observe(viewLifecycleOwner, Observer { listResource ->
                viewModel.manageResponse(listResource)
            })

            viewModel.refreshed.observe(viewLifecycleOwner, Observer {refreshed ->
                refreshed?.let{
                    if (binding.swipeUserTimeline.isRefreshing) {
                        binding.swipeUserTimeline.isRefreshing = false
                    }
                    viewModel.refreshedUsed()
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

        binding.swipeUserTimeline.setOnRefreshListener {
            viewModel.refreshList()
        }

        binding.containerItem.imgFavorite.setOnClickListener {
            viewModel.itemFavorited.value?.let{
                if (it > 0){
                    viewModel.removeFavoriteItem()
                } else {
                    viewModel.favoriteItem()
                }
            }
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

}

class DetailRepositoryViewModelFactory(
    private val application: Application,
    private val repository: Repository,
    private val owner: String,
    private val repositoryName: String
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = DetailRepositoryViewModel(application, repository, owner, repositoryName) as T
}
