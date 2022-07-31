package ru.lzanelzaz.icerock_test_task.repositories_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.AppRepository
import ru.lzanelzaz.icerock_test_task.Repo

class RepositoriesListViewModel  : ViewModel() {

    lateinit var state: MutableLiveData<State>

    sealed interface State  {
        object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data class Error(val error: String) : State
        object Empty : State
    }

    init {
        getState()
    }

    private fun getState() {
        state = MutableLiveData<State>(State.Loading)

        viewModelScope.launch {
            try {
                val repositories = AppRepository().getRepositories()
                if (repositories == emptyList<Repo>())
                    state.value = State.Empty
                else
                    state.value = State.Loaded(repositories)

            } catch (exception: Exception) {
                val error = exception.toString()
                val errorType = error.slice(0 until error.indexOf(':'))

                // "java.net.UnknownHostException" -> "Connection error"
                // else -> "Something error"

                state.value = State.Error(errorType)
            }
        }

    }
}
