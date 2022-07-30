package ru.lzanelzaz.icerock_test_task.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class AuthViewModel @Inject constructor() : ViewModel() {
//    @Inject
    lateinit var token: MutableLiveData<String>
//    val state: LiveData<State>
//    val actions: Flow<Action>

    fun onSignButtonPressed() {
    }
//
//    sealed interface State {
//        object Idle : State
//        object Loading : State
//        data class InvalidInput(val reason: String) : State
//    }
//
//    sealed interface Action {
//        data class ShowError(val message: String) : Action
//        object RouteToMain : Action
//    }

    // TODO:
}