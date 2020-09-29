package root.savaris.githubrepositories.framework.database

import root.savaris.githubrepositories.data.ILocalDataSource

class LocalDataSource(private val repositoriesDao: RepositoriesDao) : ILocalDataSource {
}