package ru.lzanelzaz.icerock_test_task.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import ru.lzanelzaz.icerock_test_task.Repo
import ru.lzanelzaz.icerock_test_task.UserInfo

private val retrofit = Retrofit.Builder()
    .baseUrl("https://api.github.com")
    .addConverterFactory(Json{ignoreUnknownKeys = true}.asConverterFactory("application/json".toMediaType()))
    .build()

// bertelledani  - user with no repositories

interface GitHubApiService {
    @GET("users/icerockdev/repos?sort=updated&per_page=10")
    suspend fun getRepo(): List<Repo>

    @GET("user")
    suspend fun getUser(@Header("Authorization") token : String): UserInfo
}

object GitHubApi {
    val retrofitService: GitHubApiService by lazy {
        retrofit.create(GitHubApiService::class.java)
    }
}