package ru.lzanelzaz.icerock_test_task.repository_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.AppRepository
import ru.lzanelzaz.icerock_test_task.KeyValueStorage
import ru.lzanelzaz.icerock_test_task.NetworkModule
import ru.lzanelzaz.icerock_test_task.model.RepoDetails
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class RepositoryInfoViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {
    private val state = MutableLiveData<State>()

    sealed interface State {
        object Loading : State
        data class Error(val error: String) : State

        data class Loaded(
            val githubRepo: RepoDetails,
            val readmeState: ReadmeState
        ) : State
    }

    var repoId: String = ""

    sealed interface ReadmeState {
        object Loading : ReadmeState
        object Empty : ReadmeState
        data class Error(val error: String) : ReadmeState
        data class Loaded(val markdown: String) : ReadmeState
    }

    fun getState(): LiveData<State> = state

    fun onRetryButtonPressed() {
        loadState()
    }

    fun onClicked() {
        loadState()
    }

    private fun loadState() {
        state.value = State.Loading
        viewModelScope.launch {
            try {
                val repository: RepoDetails = repository.getRepository(repoId)
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

    fun loadReadmeState(repo: RepoDetails) {
        viewModelScope.launch {
            state.value = State.Loaded(repo, ReadmeState.Loading)
            try {
                val readme: String = repository.getRepositoryReadme(
                    repo.owner.login,
                    repo.name,
                    repo.defaultBranch
                )
                state.value = State.Loaded(repo, ReadmeState.Loaded(readme))
            } catch (exception: Exception) {
                val errorType = exception.toString()

                val readmeState = when (errorType.slice(0 until errorType.indexOf(':'))) {
                    "retrofit2.HttpException" -> ReadmeState.Empty
                    "java.net.ConnectException" -> ReadmeState.Error("Connection error")
                    "java.net.UnknownHostException" -> ReadmeState.Error("Connection error")
                    else -> ReadmeState.Error("Something error")
                }
                state.value = State.Loaded(repo, readmeState)
            }
        }
    }
}