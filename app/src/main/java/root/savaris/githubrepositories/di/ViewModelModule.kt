package root.savaris.githubrepositories.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import root.savaris.githubrepositories.presentation.detail.DetailRepositoryViewModel
import root.savaris.githubrepositories.presentation.list.ListRepositoriesViewModel

val viewModelModule = module {

    viewModel {
        ListRepositoriesViewModel(androidApplication(), get())
    }
    viewModel { (owner : String, repository : String) ->
        DetailRepositoryViewModel(androidApplication(), owner, repository, get())
    }
}
