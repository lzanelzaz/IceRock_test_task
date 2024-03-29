@file:Suppress("JSON_FORMAT_REDUNDANT")

package ru.lzanelzaz.icerock_test_task.di

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
import ru.lzanelzaz.icerock_test_task.AppRepository
import ru.lzanelzaz.icerock_test_task.KeyValueStorage
import ru.lzanelzaz.icerock_test_task.api.GithubApiService
import ru.lzanelzaz.icerock_test_task.api.GithubRawUserContentService
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
    ): AppRepository = AppRepository(
        githubApiService = githubApiService,
        githubRawUserContentService = githubRawUserContentService,
        keyValueStorage = keyValueStorage
    )

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
