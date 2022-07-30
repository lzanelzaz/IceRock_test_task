package ru.lzanelzaz.icerock_test_task

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

class RepositoriesListViewModel : ViewModel() {

    enum class State { LOADING, ERROR, LOADED }


    lateinit var state : MutableLiveData<State>


    lateinit var _repos : MutableLiveData<List<Repo>>

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


    init {
        state = MutableLiveData<State>(State.LOADING)
        _repos = MutableLiveData<List<Repo>>()
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