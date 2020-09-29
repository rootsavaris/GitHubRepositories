package root.savaris.githubrepositories.framework.database

import root.savaris.githubrepositories.domain.RepositoryItem

fun RepositoryEntity.toDomain(): RepositoryItem {
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

fun RepositoryItem.fromDomain(): RepositoryEntity {
    return RepositoryEntity(
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