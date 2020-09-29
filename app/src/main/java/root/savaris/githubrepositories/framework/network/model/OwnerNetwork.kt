package root.savaris.githubrepositories.framework.network.model

import com.google.gson.annotations.SerializedName

data class OwnerNetwork(
    @SerializedName("id") val id: Int,
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatar_url: String
)