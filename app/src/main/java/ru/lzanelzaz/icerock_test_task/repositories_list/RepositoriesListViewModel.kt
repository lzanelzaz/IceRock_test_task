package ru.lzanelzaz.icerock_test_task.repositories_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.AppRepository
import ru.lzanelzaz.icerock_test_task.model.Repo
import javax.inject.Inject

@HiltViewModel
class RepositoriesListViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {
    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    init {
        loadState()
    }

    fun onRetryButtonPressed() {
        loadState()
    }

    fun onLogOutButtonPressed() {
        repository.logOut()
    }

    private fun loadState() {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val repositories = repository.getRepositories()
                if (repositories == emptyList<Repo>())
                    _state.value = State.Empty
                else
                    _state.value = State.Loaded(repositories)

            } catch (exception: java.net.UnknownHostException) {
                _state.value = State.Error("Connection error")
            } catch (exception: Exception) {
                _state.value = State.Error("Something error")
            }
        }
    }

    sealed interface State {
        object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data class Error(val error: String) : State
        object Empty : State
    }
}
