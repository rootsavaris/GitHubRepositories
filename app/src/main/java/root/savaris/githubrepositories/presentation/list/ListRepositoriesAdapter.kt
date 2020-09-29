package root.savaris.githubrepositories.presentation.list

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
import root.savaris.githubrepositories.databinding.AdapterLoadMoreBinding
import root.savaris.githubrepositories.databinding.AdapterRepositoryLineBinding
import root.savaris.githubrepositories.utils.ListItemType.Companion.DEFAULT_VIEW
import root.savaris.githubrepositories.utils.ListItemType.Companion.FOOTER_CONTENT
import root.savaris.githubrepositories.utils.ListItemType.Companion.FOOTER_VIEW
import root.savaris.githubrepositories.utils.LoadMoreViewHolder
import root.savaris.githubrepositories.utils.RepositoryClickListener

class ListRepositoriersAdapter(private val clickListener: RepositoryClickListener) : ListAdapter<RepositoryDataItem, RecyclerView.ViewHolder>(diffCallback) {

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
            val items = when (list) {
                null -> listOf(RepositoryDataItem.Footer)
                else -> {
                    if (list.isNotEmpty() && list[list.size - 1].id == FOOTER_CONTENT) {
                        list.map { RepositoryDataItem.RepositoryItemAdapter(it) }.filter { it.id != FOOTER_CONTENT } + listOf(RepositoryDataItem.Footer)
                    } else {
                        list.map { RepositoryDataItem.RepositoryItemAdapter(it) }
                    }
                }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    open class RepositoryViewHolder(val binding: AdapterRepositoryLineBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(
            item: RepositoryItem,
            clickListener: RepositoryClickListener
        ) {
            binding.repository = item
            binding.executePendingBindings()
            binding.clickListener = clickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == DEFAULT_VIEW) {
            val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.adapter_repository_line, parent, false)
            RepositoryViewHolder(binding as AdapterRepositoryLineBinding)
        } else {
            val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.adapter_load_more, parent, false)
            LoadMoreViewHolder(binding as AdapterLoadMoreBinding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RepositoryDataItem.Footer -> FOOTER_VIEW
            is RepositoryDataItem.RepositoryItemAdapter -> DEFAULT_VIEW
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is RepositoryViewHolder -> {
                if (itemCount > 0) {
                    val repositoryItem = (getItem(position) as RepositoryDataItem.RepositoryItemAdapter).repository
                    viewHolder.bind(repositoryItem, clickListener)
                }
            }
        }
    }

    fun isLoadingEnabled(): Boolean {
        return if (itemCount > 0) {
            getItem(itemCount - 1)?.id == FOOTER_CONTENT
        } else {
            false
        }
    }

}

sealed class RepositoryDataItem {
    data class RepositoryItemAdapter(val repository: RepositoryItem) : RepositoryDataItem() {
        override val id = repository.id ?: 0
    }
    object Footer : RepositoryDataItem() {
        override val id = 0
    }
    abstract val id: Int
}



