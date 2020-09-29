package root.savaris.githubrepositories.presentation.detail

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.layout_repository_detail.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import root.savaris.githubrepositories.R
import root.savaris.githubrepositories.databinding.FragmentDetailRepositoryBinding

class DetailRepositoryFragment : Fragment() {

    private lateinit var binding: FragmentDetailRepositoryBinding
    private val viewModel: DetailRepositoryViewModel by viewModel{parametersOf(DetailRepositoryFragmentArgs.fromBundle(arguments!!).owner, DetailRepositoryFragmentArgs.fromBundle(arguments!!).repository)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate<FragmentDetailRepositoryBinding>(inflater,
            R.layout.fragment_detail_repository,container,false)

        activity?.let {

            loadViews()

            viewModel.state.observe(this, Observer { states ->
                states?.let {

                    if (it.isLoading){
                        binding.shimmerContainer.startShimmer()
                        binding.shimmerContainer.visibility = View.VISIBLE
                    } else {
                        binding.shimmerContainer.stopShimmer()
                        binding.shimmerContainer.visibility = View.GONE
                    }

                    binding.containerItem.root.visibility = if (it.containerRecyclerVisible) View.VISIBLE else View.GONE
                    binding.containerError.root.visibility = if (it.containerErrorLayoutVisible) View.VISIBLE else View.GONE

                    if (it.showSnackBar) {
                        showSnackError(it.messageError)
                    } else if (it.containerErrorLayoutVisible && !it.showSnackBar) {
                        binding.containerError.txtErrorMsg.text = it.messageError
                    }

                    if (binding.swipeUserTimeline.isRefreshing) {
                        binding.swipeUserTimeline.isRefreshing = false
                    }

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

            viewModel.itemFavorited.observe(viewLifecycleOwner, Observer { count ->
                count?.let {
                    if (count > 0){
                        binding.containerItem.imgFavorite.setColorFilter(ContextCompat.getColor(activity!!, android.R.color.holo_orange_light))
                    } else {
                        binding.containerItem.imgFavorite.setColorFilter(ContextCompat.getColor(activity!!, android.R.color.darker_gray))
                    }
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
