package ru.lzanelzaz.icerock_test_task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.network.GitHubApi

class OverviewViewModel : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message : LiveData<String> = _message

    init {
        getGitHubRepo()
    }

    private fun getGitHubRepo() {
        viewModelScope.launch {
            try {
                _message.value = GitHubApi.retrofitService.getRepo()
            } catch (e : Exception) {
                _message.value = e.message
            }
        }
    }
}