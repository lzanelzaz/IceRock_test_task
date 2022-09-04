package ru.lzanelzaz.icerock_test_task.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import ru.lzanelzaz.icerock_test_task.model.Repo
import ru.lzanelzaz.icerock_test_task.model.RepoDetails
import ru.lzanelzaz.icerock_test_task.model.UserInfo
import javax.inject.Singleton

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