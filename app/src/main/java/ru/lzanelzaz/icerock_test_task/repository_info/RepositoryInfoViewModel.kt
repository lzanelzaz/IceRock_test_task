package ru.lzanelzaz.icerock_test_task.repository_info

import androidx.constraintlayout.motion.widget.Debug.getState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.AppRepository
import ru.lzanelzaz.icerock_test_task.RepoDetails

class RepositoryInfoViewModel(private val repoId: String) : ViewModel() {
    private val state: MutableLiveData<State> by lazy {
        MutableLiveData<State>(State.Loading).also { loadState() }
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

    fun getState(): LiveData<State> = state

    private fun loadState() {
        viewModelScope.launch {
            try {
                val repository: RepoDetails = AppRepository().getRepository(repoId)
                state.value = State.Loaded(repository, ReadmeState.Loading)
                try {
                    val readme : String = AppRepository().getRepositoryReadme(repository.owner.login, repository.name, repository.defaultBranch)
                    state.value = State.Loaded(repository, ReadmeState.Loaded(readme))

                } catch (exception: Exception) {
                    val error = exception.toString()
                    val errorType = error.slice(0 until error.indexOf(':'))
                    // "retrofit2.HttpException" -> "no readme"
                    state.value = State.Loaded(repository,
                        if (errorType == "retrofit2.HttpException")
                            ReadmeState.Empty
                        else ReadmeState.Error(errorType)
                    )
                }

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