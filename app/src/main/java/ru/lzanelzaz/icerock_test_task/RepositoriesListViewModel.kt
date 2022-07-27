package ru.lzanelzaz.icerock_test_task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.network.GitHubApi

class RepositoriesListViewModel : ViewModel() {
    /*val state: LiveData<State>

    sealed interface State {
        object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data class Error(val error: String) : State
        object Empty : State
    }*/

    private val _repos = MutableLiveData<List<Repo>>()
    val repos : LiveData<List<Repo>> = _repos

    init {
        getGitHubRepo()
    }

    private fun getGitHubRepo() {
        viewModelScope.launch {
            try {
                _repos.value = GitHubApi.retrofitService.getRepo()
            } catch (e : Exception) {
                println(e.message)
            }
        }
    }
}