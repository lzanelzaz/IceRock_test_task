package ru.lzanelzaz.icerock_test_task.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import ru.lzanelzaz.icerock_test_task.Repo

private val retrofit = Retrofit.Builder()
    .baseUrl("https://api.github.com")
    .addConverterFactory(Json{ignoreUnknownKeys = true}.asConverterFactory("application/json".toMediaType()))
    .build()

interface GitHubApiService {
    @Headers("Authorization: token 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")
    @GET("users/icerockdev/repos")
    suspend fun getRepo(): List<Repo>
}

object GitHubApi {
    val retrofitService: GitHubApiService by lazy {
        retrofit.create(GitHubApiService::class.java)
    }
}