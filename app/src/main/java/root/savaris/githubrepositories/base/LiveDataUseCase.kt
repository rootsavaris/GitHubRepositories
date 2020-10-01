package root.savaris.githubrepositories.base


import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

abstract class LiveDataUseCase<T, in Params> constructor(
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) {
    abstract fun buildUseCaseFlowable(params: Params): LiveData<T>

    open fun execute(
        params: Params
    ) : LiveData<T> {
        return buildUseCaseFlowable(params)
    }
}
