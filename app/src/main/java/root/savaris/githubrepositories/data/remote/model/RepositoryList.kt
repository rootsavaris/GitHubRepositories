package root.savaris.githubrepositories.data.remote.model

import com.google.gson.annotations.SerializedName

data class RepositoryList(
    @SerializedName("items") val items: List<RepositoryRemoteItemEntity>
)
