package root.savaris.githubrepositories.framework.network

import root.savaris.githubrepositories.domain.RepositoryItem
import root.savaris.githubrepositories.framework.network.model.RepositoryNetwork

fun RepositoryNetwork.toDomain(): RepositoryItem {
    return RepositoryItem(
        id = this.id,
        name = this.name,
        fullName = this.fullName,
        isPrivate = this.isPrivate,
        ownerId = this.owner.id,
        ownerLogin = this.owner.login,
        ownerAvatar = this.owner.avatar_url,
        description = this.description,
        stargazersCount = this.stargazersCount,
        watchersCount = this.watchersCount,
        subscribers_count = this.subscribers_count,
        forks_count = this.forks_count
    )
}