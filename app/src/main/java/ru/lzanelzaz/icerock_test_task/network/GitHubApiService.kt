package ru.lzanelzaz.icerock_test_task.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val GITHUB_API_URI = "https://api.github.com"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(GITHUB_API_URI)
    .build()

interface GitHubApiService {
    @GET("zen")
    suspend fun getRepo() : String
}

object GitHubApi {
    val retrofitService : GitHubApiService by lazy {
        retrofit.create(GitHubApiService::class.java)
    }
}