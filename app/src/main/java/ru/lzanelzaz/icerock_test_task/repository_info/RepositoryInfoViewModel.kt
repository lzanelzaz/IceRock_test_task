package ru.lzanelzaz.icerock_test_task.repository_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.AppRepository
import ru.lzanelzaz.icerock_test_task.model.RepoDetails
import javax.inject.Inject

@HiltViewModel
class RepositoryInfoViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {
    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    var repoId: String = ""
        set(value) {
            field = value
            loadState()
        }

    fun onRetryButtonPressed() {
        loadState()
    }

    fun logOut() {
        repository.logOut()
    }

    fun loadReadmeState(repo: RepoDetails) {
        viewModelScope.launch {
            _state.value = State.Loaded(repo, ReadmeState.Loading)
            try {
                val readme: String = repository.getRepositoryReadme(
                    repo.owner.login,
                    repo.name,
                    repo.defaultBranch
                )
                _state.value = State.Loaded(repo, ReadmeState.Loaded(readme))
            } catch (exception: Exception) {
                val errorType = exception.toString()
                val readmeState = when (errorType.slice(0 until errorType.indexOf(':'))) {
                    "retrofit2.HttpException" -> ReadmeState.Empty
                    "java.net.ConnectException" -> ReadmeState.Error("Connection error")
                    "java.net.UnknownHostException" -> ReadmeState.Error("Connection error")
                    else -> ReadmeState.Error("Something error")
                }
                _state.value = State.Loaded(repo, readmeState)
            }
        }
    }

    private fun loadState() {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val repository: RepoDetails = repository.getRepository(repoId)
                loadReadmeState(repository)
            } catch (exception: Exception) {
                val errorType = exception.toString()
                val reason = when (errorType.slice(0 until errorType.indexOf(':'))) {
                    "java.net.UnknownHostException" -> "Connection error"
                    else -> "Something error"
                }
                _state.value = State.Error(reason)
            }
        }
    }

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
}