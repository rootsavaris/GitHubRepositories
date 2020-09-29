package root.savaris.githubrepositories.framework.network.model

import com.google.gson.annotations.SerializedName

data class RepositoryNetwork(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("private") val isPrivate: Boolean,
    @SerializedName("owner") val owner: OwnerNetwork,
    @SerializedName("description") val description: String,
    @SerializedName("stargazers_count") val stargazersCount: Int,
    @SerializedName("watchers_count") val watchersCount: Int,
    @SerializedName("subscribers_count") val subscribers_count: Int,
    @SerializedName("forks_count") val forks_count: Int
)
