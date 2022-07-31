package ru.lzanelzaz.icerock_test_task.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.AppRepository
import ru.lzanelzaz.icerock_test_task.KeyValueStorage
import ru.lzanelzaz.icerock_test_task.repositories_list.RepositoriesListViewModel
import javax.inject.Inject
import javax.inject.Singleton

class AuthViewModel @Inject constructor() : ViewModel() {
    //    @Inject
    val token = MutableLiveData<String>(KeyValueStorage.authToken)
    var state = MutableLiveData<State>(State.Loading)
//    val actions: Flow<Action>

    fun onSignButtonPressed() {
        viewModelScope.launch {
            try {
                val user = AppRepository().signIn(token.value!!)
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
//
//    sealed interface Action {
//        data class ShowError(val message: String) : Action
//        object RouteToMain : Action
//    }

    // TODO:
}