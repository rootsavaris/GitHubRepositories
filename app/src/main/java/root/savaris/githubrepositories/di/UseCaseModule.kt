package root.savaris.githubrepositories.di

import org.koin.dsl.module
import root.savaris.githubrepositories.interactors.GetRepositoriesUseCase
import root.savaris.githubrepositories.interactors.GetRepositoryUseCase

val useCaseModule = module {
    factory {
        GetRepositoriesUseCase(get())
    }
    factory {
        GetRepositoryUseCase(get())
    }
}
