package root.savaris.githubrepositories.framework.network.model

import com.google.gson.annotations.SerializedName

data class RepositoryListNetwork(
    @SerializedName("items") val items: List<RepositoryNetwork>
)
