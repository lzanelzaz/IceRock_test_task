package ru.lzanelzaz.icerock_test_task.repositories_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.AppRepository
import ru.lzanelzaz.icerock_test_task.NetworkModule
import ru.lzanelzaz.icerock_test_task.model.Repo
import javax.inject.Inject

@HiltViewModel
class RepositoriesListViewModel @Inject constructor(): ViewModel() {

    private val state = MutableLiveData<State>()

    sealed interface State {
        object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data class Error(val error: String) : State
        object Empty : State
    }

    fun getState() : LiveData<State> = state

    fun onRetryButtonPressed() {
        loadState()
    }

    init {
        loadState()
    }

    private fun loadState() {
        state.value = State.Loading
        viewModelScope.launch {
            try {
                val repositories = NetworkModule.getAppRepository().getRepositories()
                if (repositories == emptyList<Repo>())
                    state.value = State.Empty
                else
                    state.value = State.Loaded(repositories)

            } catch (exception: Exception) {
                val errorType = exception.toString()

                val reason = when(errorType.slice(0 until errorType.indexOf(':'))) {
                    "java.net.UnknownHostException" -> "Connection error"
                    else -> "Something error"
                }
                state.value = State.Error(reason)
            }
        }
    }
}
