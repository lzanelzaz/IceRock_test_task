package ru.lzanelzaz.icerock_test_task.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.AppRepository
import ru.lzanelzaz.icerock_test_task.KeyValueStorage
import ru.lzanelzaz.icerock_test_task.NetworkModule
import ru.lzanelzaz.icerock_test_task.R
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AppRepository): ViewModel() {
    private val token = MutableLiveData<String>()
    var state = MutableLiveData<State>(State.Loading)

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    fun onSignButtonPressed() {
        viewModelScope.launch {
            try {
                repository.signIn(token.value.toString())
                _actions.send(Action.RouteToMain)
            } catch (exception: Exception) {
                val errorType = exception.toString()
                val reason = when (errorType.slice(0 until errorType.indexOf(':'))) {
                    "retrofit2.HttpException" -> "Error data"
                    "java.net.UnknownHostException" -> "Connection error"
                    else -> "Invalid token"
                }
                state.value = when (reason) {
                    "Invalid token" -> State.InvalidInput(reason)
                    else -> {
                        _actions.send(Action.ShowError(reason))
                        State.Idle
                    }
                }
            }
        }
    }

    fun setToken(userToken: String) {
        token.value = userToken
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