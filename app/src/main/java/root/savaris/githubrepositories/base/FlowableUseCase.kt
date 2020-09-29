package root.savaris.githubrepositories.base


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

abstract class FlowableUseCase<T, in Params> constructor(
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) {
    abstract suspend fun buildUseCaseFlowable(params: Params): Flow<T>

    open suspend fun execute(
        params: Params,
        onStart: (() -> Unit)? = null,
        onNext: (T) -> Unit,
        onError: (e: Throwable) -> Unit
    ){
        buildUseCaseFlowable(params)
            .onStart{
                onStart?.invoke()
            }
            .catch {
                onError.invoke(it)
            }
            .flowOn(coroutineContext)
            .collect {
                onNext.invoke(it)
            }
    }
}
