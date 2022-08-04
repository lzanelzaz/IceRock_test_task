package ru.lzanelzaz.icerock_test_task.repository_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.AppRepository
import ru.lzanelzaz.icerock_test_task.model.RepoDetails

class RepositoryInfoViewModel (private val repoId: String) : ViewModel() {
    private val state = MutableLiveData<State>()

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

    fun getState(): LiveData<State> = state

    fun updateState() {
        loadState()
    }

    init {
        loadState()
    }

    private fun loadState() {
        state.value = State.Loading
        viewModelScope.launch {
            try {
                val repository: RepoDetails = AppRepository().getRepository(repoId)
                state.value = State.Loaded(repository, ReadmeState.Loading)
                loadReadmeState(repository)

            } catch (exception: Exception) {
                val errorType = exception.toString()

                val reason = when (errorType.slice(0 until errorType.indexOf(':'))) {
                    "java.net.UnknownHostException" -> "Connection error"
                    else -> "Something error"
                }
                state.value = State.Error(reason)
            }
        }
    }

    fun loadReadmeState(repository: RepoDetails) {
        viewModelScope.launch {
            state.value = State.Loaded(repository, ReadmeState.Loading)
            try {
                val readme: String = AppRepository().getRepositoryReadme(
                    repository.owner.login,
                    repository.name,
                    repository.defaultBranch
                )
                state.value = State.Loaded(repository, ReadmeState.Loaded(readme))
            } catch (exception: Exception) {
                val errorType = exception.toString()

                val readmeState = when (errorType.slice(0 until errorType.indexOf(':'))) {
                    "retrofit2.HttpException" -> ReadmeState.Empty
                    "java.net.ConnectException" -> ReadmeState.Error("Connection error")
                    "java.net.UnknownHostException" -> ReadmeState.Error("Connection error")
                    else -> ReadmeState.Error("Something error")
                }
                state.value = State.Loaded(repository, readmeState)
            }
        }
    }
}