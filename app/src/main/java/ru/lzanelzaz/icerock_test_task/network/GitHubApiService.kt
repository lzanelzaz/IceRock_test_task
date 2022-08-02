package ru.lzanelzaz.icerock_test_task.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import ru.lzanelzaz.icerock_test_task.KeyValueStorage
import ru.lzanelzaz.icerock_test_task.Repo
import ru.lzanelzaz.icerock_test_task.RepoDetails
import ru.lzanelzaz.icerock_test_task.UserInfo

private val json = Json {
    ignoreUnknownKeys = true
}

private val retrofit = Retrofit.Builder()
    .baseUrl("https://api.github.com")
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()

// bertelledani  - user with no repositories

interface GitHubApiService {
    @GET("user")
    suspend fun signIn(@Header("Authorization") token: String): UserInfo

    @GET("users/icerockdev/repos?sort=updated&per_page=10")
    suspend fun getRepositories(@Header("Authorization") token: String): List<Repo>

    @GET("repos/icerockdev/{repoId}")
    suspend fun getRepository(@Header("Authorization") token: String, @Path("repoId") repoId: String): RepoDetails

    @GET("https://raw.githubusercontent.com/{ownerName}/{repositoryName}/{branchName}/README.md")
    suspend fun getRepositoryReadme(
        @Path("ownerName") ownerName: String,
        @Path("repositoryName") repositoryName: String,
        @Path("branchName") branchName: String
    ): String
}

object GitHubApi {
    val retrofitService: GitHubApiService by lazy {
        retrofit.create(GitHubApiService::class.java)
    }
}