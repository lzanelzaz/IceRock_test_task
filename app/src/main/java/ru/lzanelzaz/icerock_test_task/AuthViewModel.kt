package ru.lzanelzaz.icerock_test_task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow

class AuthViewModel : ViewModel() {
//    val token: MutableLiveData<String>
//    val state: LiveData<State>
//    val actions: Flow<Action>

    fun onSignButtonPressed() {
        println("pressed")
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

    // TODO:
}