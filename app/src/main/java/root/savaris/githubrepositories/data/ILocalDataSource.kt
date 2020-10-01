package root.savaris.githubrepositories.data

import androidx.lifecycle.LiveData

interface ILocalDataSource {
    fun isRepositoryFavorite(name: String, ownerLogin: String): LiveData<Int>
}