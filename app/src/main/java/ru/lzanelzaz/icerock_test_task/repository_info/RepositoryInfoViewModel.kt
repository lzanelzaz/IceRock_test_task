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
        if (_state.value is State.Loaded)
            loadReadmeState((_state.value as State.Loaded).githubRepo)
        else
            loadState()
    }

    fun onLogOutButtonPressed() {
        repository.logOut()
    }

    private fun loadReadmeState(repo: RepoDetails) {
        viewModelScope.launch {
            _state.value = State.Loaded(repo, ReadmeState.Loading)
            val readmeState = try {
                val readme: String = repository.getRepositoryReadme(
                    ownerName = repo.owner.login,
                    repositoryName = repo.name,
                    branchName = repo.defaultBranch
                )
                ReadmeState.Loaded(readme)
            } catch (exception: retrofit2.HttpException) {
                ReadmeState.Empty
            } catch (exception: java.net.ConnectException) {
                ReadmeState.Error("Connection error")
            } catch (exception: java.net.UnknownHostException) {
                ReadmeState.Error("Connection error")
            } catch (exception: Exception) {
                ReadmeState.Error("Something error")
            }
            _state.value = State.Loaded(repo, readmeState)
        }
    }

    private fun loadState() {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val repository: RepoDetails = repository.getRepository(repoId)
                loadReadmeState(repository)
            } catch (exception: java.net.UnknownHostException) {
                _state.value = State.Error("Connection error")
            } catch (exception: Exception) {
                _state.value = State.Error("Something error")
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