package root.savaris.githubrepositories.data.remote.model

import com.google.gson.annotations.SerializedName

data class OwnerItemEntity(
    @SerializedName("id") val id: Int,
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatar_url: String
)