package root.savaris.githubrepositories.utils

import root.savaris.githubrepositories.domain.RepositoryItem

class RepositoryClickListener(val clickListener: (repositoryOwner: String, repositoryName: String) -> Unit) {
    fun onClick(repositoryItem: RepositoryItem) = clickListener(repositoryItem.ownerLogin ?: "", repositoryItem.name ?: "")
}