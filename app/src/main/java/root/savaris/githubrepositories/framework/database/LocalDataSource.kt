package root.savaris.githubrepositories.framework.database

import androidx.lifecycle.LiveData
import root.savaris.githubrepositories.data.ILocalDataSource

class LocalDataSource(private val repositoriesDao: RepositoriesDao) : ILocalDataSource {
    override fun isRepositoryFavorite(name: String, ownerLogin: String): LiveData<Int> {
        return repositoriesDao.isRepositoryFavorite(name, ownerLogin)
    }
}