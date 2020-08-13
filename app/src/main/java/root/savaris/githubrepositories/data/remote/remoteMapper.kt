package root.savaris.githubrepositories.data.remote

import root.savaris.githubrepositories.data.RepositoryItem
import root.savaris.githubrepositories.data.remote.model.RepositoryRemoteItemEntity

fun RepositoryRemoteItemEntity.toDomain(): RepositoryItem {
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