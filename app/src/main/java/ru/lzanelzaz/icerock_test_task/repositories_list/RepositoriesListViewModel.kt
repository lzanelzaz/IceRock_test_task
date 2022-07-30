package ru.lzanelzaz.icerock_test_task.repositories_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.lzanelzaz.icerock_test_task.KeyValueStorage
import ru.lzanelzaz.icerock_test_task.Repo
import ru.lzanelzaz.icerock_test_task.network.GitHubApi

class RepositoriesListViewModel : ViewModel() {

    lateinit var state: MutableLiveData<State>

    sealed interface State {
        object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data class Error(val error: String) : State
        object Empty : State
    }

    init {
        state = MutableLiveData<State>()
        getState()
    }

    private fun getState() {
        state.value = State.Loading

        GitHubApi.retrofitService.getRepo(KeyValueStorage().authToken).enqueue(object :
            Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        state.value = State.Loaded(responseBody)
                    } else {
                        state.value = State.Empty
                    }
                } else {
                    state.value = State.Error("Something error")

                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                state.value = State.Error("Connection error")
            }
        })

    }
}
