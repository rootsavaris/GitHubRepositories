package root.savaris.githubrepositories.data.remote.model

import com.google.gson.annotations.SerializedName

data class RepositoryRemoteItemEntity(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("private") val isPrivate: Boolean,
    @SerializedName("owner") val owner: OwnerItemEntity,
    @SerializedName("description") val description: String,
    @SerializedName("stargazers_count") val stargazersCount: Int,
    @SerializedName("watchers_count") val watchersCount: Int,
    @SerializedName("subscribers_count") val subscribers_count: Int,
    @SerializedName("forks_count") val forks_count: Int
)
