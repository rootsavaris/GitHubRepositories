package root.savaris.githubrepositories.base


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

abstract class BaseViewModel<ViewState : BaseViewState, ViewAction : BaseAction>(application: Application, initialState: ViewState) : AndroidViewModel(application){

    private val _state = MutableLiveData<ViewState>()
    val state : LiveData<ViewState>
        get()  = _state

    fun sendAction(viewAction: ViewAction) {
        _state.postValue(onReduceState(viewAction))
    }

    fun stateLoaded(){
        _state.postValue(null)
    }

    protected abstract fun onReduceState(viewAction: ViewAction): ViewState

}