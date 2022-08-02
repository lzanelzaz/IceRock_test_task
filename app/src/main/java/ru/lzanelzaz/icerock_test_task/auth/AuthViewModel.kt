package ru.lzanelzaz.icerock_test_task.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.AppRepository
import javax.inject.Inject

class AuthViewModel @Inject constructor() : ViewModel() {
    //    @Inject
    val token = MutableLiveData<String>()
    var state = MutableLiveData<State>(State.Loading)

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    fun onSignButtonPressed() {
        viewModelScope.launch {
            try {
                AppRepository().signIn(token.value!!)
                state.value = State.Idle
            } catch (exception: Exception) {
                val error = exception.toString()
                val errorType = error.slice(0 until error.indexOf(':'))

                // "retrofit2.HttpException " -> Invalid token
                // else -> Error data / error code information for developers
                state.value = State.InvalidInput(errorType)
            }
        }
    }

    sealed interface State {
        object Idle : State
        object Loading : State
        data class InvalidInput(val reason: String) : State
    }

    sealed interface Action {
        data class ShowError(val message: String) : Action
        object RouteToMain : Action
    }

}