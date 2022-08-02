package ru.lzanelzaz.icerock_test_task.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
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

private val apiGithub = Retrofit.Builder()
    .baseUrl("https://api.github.com")
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()

interface GithubApiService {
    @GET("user")
    suspend fun signIn(@Header("Authorization") token: String): UserInfo

    // bertelledani  - user with no repositories
    // orgs/echo-health/repos EchoTestApp - no readme

    @GET("users/icerockdev/repos?sort=updated&per_page=10")
    suspend fun getRepositories(@Header("Authorization") token: String): List<Repo>

    @GET("repos/icerockdev/{repoId}")
    suspend fun getRepository(
        @Header("Authorization") token: String,
        @Path("repoId") repoId: String
    ): RepoDetails
}

object GithubApi {
    val retrofitService: GithubApiService by lazy {
        apiGithub.create(GithubApiService::class.java)
    }
}

private val rawGithubUserContent = Retrofit.Builder()
    .baseUrl("https://raw.githubusercontent.com")
    .addConverterFactory(ScalarsConverterFactory.create())
    .build()

interface GithubRawUserContentService {
    @GET("{ownerName}/{repositoryName}/{branchName}/README.md")
    suspend fun getRepositoryReadme(
        @Path("ownerName") ownerName: String,
        @Path("repositoryName") repositoryName: String,
        @Path("branchName") branchName: String
    ): String
}

object GithubRawUserContent {
    val retrofitService: GithubRawUserContentService by lazy {
        rawGithubUserContent.create(GithubRawUserContentService::class.java)
    }
}