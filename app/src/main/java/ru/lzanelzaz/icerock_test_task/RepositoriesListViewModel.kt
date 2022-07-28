package ru.lzanelzaz.icerock_test_task

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RepositoriesListViewModel : ViewModel() {

    enum class State { LOADING, ERROR, LOADED }

    val state : MutableLiveData<State> = MutableLiveData<State>(State.LOADING)

//    sealed interface State {
//        object Loading : State
//        data class Loaded(val repos: List<Repo>) : State
//        data class Error(val error: String) : State
//        object Empty : State
//    }
//

   /* private val repos: MutableLiveData<List<Repo>> by lazy {
        MutableLiveData<List<Repo>>().also {
            loadRepositories()
        }
    }

    fun getRepositories(): LiveData<List<Repo>> {
        return repos
    }

    private fun loadRepositories() {
        viewModelScope.launch {
            try {
                repos.value = AppRepository().getRepositories()
            } catch (e : Exception) {
                println(e.message)
            }
        }
    }*/

    private val _repos = MutableLiveData<List<Repo>>()

    init {
        getGitHubRepo()
    }

    fun getRepositories() = _repos

    private fun getGitHubRepo() {
        viewModelScope.launch {
            try {
                _repos.value = AppRepository().getRepositories()
                state.value = State.LOADED
            } catch (e : Exception) {
                state.value = State.ERROR
                println(e.message)
            }
        }
    }
}