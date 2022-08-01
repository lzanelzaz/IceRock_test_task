package ru.lzanelzaz.icerock_test_task.repository_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.AppRepository
import ru.lzanelzaz.icerock_test_task.RepoDetails

class RepositoryInfoViewModel(private val repoId: String) : ViewModel() {
    var state = MutableLiveData<State>(State.Loading)

    sealed interface State {
        object Loading : State
        data class Error(val error: String) : State

        data class Loaded(
            val githubRepo: RepoDetails,
            val readmeState: ReadmeState
        ) : State
    }

    sealed interface ReadmeState {
        object Loading : ReadmeState
        object Empty : ReadmeState
        data class Error(val error: String) : ReadmeState
        data class Loaded(val markdown: String) : ReadmeState
    }

init {
    getState()
}
    private fun getState() {
        viewModelScope.launch {
            try {
                val repository: RepoDetails = AppRepository().getRepository(repoId)
                state.value = State.Loaded(repository, ReadmeState.Loading)
                //val readme = AppRepository().getRepositoryReadme("icerockdev", repository.name, repository.defaultBranch)
                //val readmeState =

            } catch (exception: Exception) {
                val error = exception.toString()
                val errorType = error.slice(0 until error.indexOf(':'))

                // "java.net.UnknownHostException" -> "Connection error"
                // else -> "Something error"

                state.value = RepositoryInfoViewModel.State.Error(errorType)
            }

        }
    }
}