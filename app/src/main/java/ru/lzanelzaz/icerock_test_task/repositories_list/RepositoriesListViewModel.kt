package ru.lzanelzaz.icerock_test_task.repositories_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.AppRepository
import ru.lzanelzaz.icerock_test_task.models.Repo

class RepositoriesListViewModel : ViewModel() {

    private val state: MutableLiveData<State> by lazy {
        MutableLiveData<State>(State.Loading).also { loadState() }
    }

    sealed interface State {
        object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data class Error(val error: String) : State
        object Empty : State
    }

    fun getState() : LiveData<State> = state

    private fun loadState() {
        viewModelScope.launch {
            try {
                val repositories = AppRepository().getRepositories()
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
