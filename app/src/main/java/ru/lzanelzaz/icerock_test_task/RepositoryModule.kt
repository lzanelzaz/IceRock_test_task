@file:Suppress("JSON_FORMAT_REDUNDANT")

package ru.lzanelzaz.icerock_test_task

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import ru.lzanelzaz.icerock_test_task.model.Repo
import ru.lzanelzaz.icerock_test_task.model.RepoDetails
import ru.lzanelzaz.icerock_test_task.model.UserInfo
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideKeyValueStorage(@ApplicationContext context: Context): KeyValueStorage =
        KeyValueStorage(context)

    @Provides
    @Singleton
    fun provideAppRepository(
        githubApiService: GithubApiService,
        githubRawUserContentService: GithubRawUserContentService,
        keyValueStorage: KeyValueStorage
    ): AppRepository = AppRepository(githubApiService, githubRawUserContentService, keyValueStorage)

    @Provides
    @Singleton
    fun provideGithubApiService(): GithubApiService = Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .addConverterFactory(Json {
            ignoreUnknownKeys = true
        }.asConverterFactory("application/json".toMediaType()))
        .build().create(GithubApiService::class.java)

    @Provides
    @Singleton
    fun provideGithubRawUserContentService(): GithubRawUserContentService = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build().create(GithubRawUserContentService::class.java)
}

@Singleton
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

@Singleton
interface GithubRawUserContentService {
    @GET("{ownerName}/{repositoryName}/{branchName}/README.md")
    suspend fun getRepositoryReadme(
        @Path("ownerName") ownerName: String,
        @Path("repositoryName") repositoryName: String,
        @Path("branchName") branchName: String
    ): String
}
