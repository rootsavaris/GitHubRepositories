package root.savaris.githubrepositories.presentation.favorites

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import root.savaris.githubrepositories.R
import root.savaris.githubrepositories.domain.RepositoryItem
import root.savaris.githubrepositories.databinding.AdapterRepositoryFavoriteLineBinding
import root.savaris.githubrepositories.utils.RepositoryClickListener

class ListRepositoriesFavoritesAdapter(private val activity: Activity, private val clickListener: RepositoryClickListener, private val clickRemoveListener: RepositoryRemoveClickListener) : ListAdapter<RepositoryDataItem, RecyclerView.ViewHolder>(
    diffCallback
) {

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<RepositoryDataItem>() {
            override fun areItemsTheSame(oldItem: RepositoryDataItem, newItem: RepositoryDataItem): Boolean =
                    oldItem.id == newItem.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: RepositoryDataItem, newItem: RepositoryDataItem): Boolean =
                    oldItem == newItem
        }

    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun submitNewList(list: List<RepositoryItem>) {
        adapterScope.launch {
            val items = list.map {
                RepositoryDataItem.RepositoryItemAdapter(
                    it
                )
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    open class RepositoryFavoriteViewHolder(val binding: AdapterRepositoryFavoriteLineBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(
            item: RepositoryItem,
            clickListener: RepositoryClickListener,
            clickRemoveListener: RepositoryRemoveClickListener
        ) {
            binding.repository = item
            binding.executePendingBindings()
            binding.clickListener = clickListener
            binding.clickRemoveListener = clickRemoveListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.adapter_repository_favorite_line, parent, false)
        return RepositoryFavoriteViewHolder(
            binding as AdapterRepositoryFavoriteLineBinding
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is RepositoryFavoriteViewHolder -> {
                if (itemCount > 0) {
                    val repositoryItem = (getItem(position) as RepositoryDataItem.RepositoryItemAdapter).repository
                    viewHolder.bind(repositoryItem, clickListener, clickRemoveListener)
                }
            }
        }
    }

}

sealed class RepositoryDataItem {
    data class RepositoryItemAdapter(val repository: RepositoryItem) : RepositoryDataItem() {
        override val id = repository.id ?: 0
    }
    abstract val id: Int
}

class RepositoryRemoveClickListener(val clickListener: (repositoryItem: RepositoryItem) -> Unit) {
    fun onClick(repositoryItem: RepositoryItem) = clickListener(repositoryItem)
}




