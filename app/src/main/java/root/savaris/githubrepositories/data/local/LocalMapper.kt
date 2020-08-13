package root.savaris.githubrepositories.data.local

import root.savaris.githubrepositories.data.RepositoryItem

fun RepositoryLocalItemEntity.toDomain(): RepositoryItem {
    return RepositoryItem(
        id = this.id,
        name = this.name,
        fullName = this.fullName,
        isPrivate = this.isPrivate,
        ownerId = this.ownerId,
        ownerLogin = this.ownerLogin,
        ownerAvatar = this.ownerAvatar,
        description = this.description,
        stargazersCount = this.stargazersCount,
        watchersCount = this.watchersCount,
        subscribers_count = this.subscribers_count,
        forks_count = this.forks_count
    )
}

fun RepositoryItem.fromDomain(): RepositoryLocalItemEntity {
    return RepositoryLocalItemEntity(
        id = this.id,
        name = this.name,
        fullName = this.fullName,
        isPrivate = this.isPrivate,
        ownerId = this.ownerId,
        ownerLogin = this.ownerLogin,
        ownerAvatar = this.ownerAvatar,
        description = this.description,
        stargazersCount = this.stargazersCount,
        watchersCount = this.watchersCount,
        subscribers_count = this.subscribers_count,
        forks_count = this.forks_count
    )
}