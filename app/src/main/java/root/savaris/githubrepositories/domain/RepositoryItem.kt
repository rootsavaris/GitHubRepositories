package root.savaris.githubrepositories.domain

data class RepositoryItem(
    val id: Int?=null,
    val name: String?=null,
    val fullName: String?=null,
    val isPrivate: Boolean?=null,
    val ownerId: Int?=null,
    val ownerLogin: String?=null,
    val ownerAvatar: String?=null,
    val description: String?=null,
    val stargazersCount: Int?=null,
    val watchersCount: Int?=null,
    val subscribers_count: Int?=null,
    val forks_count: Int?=null
)
