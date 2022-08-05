package ru.lzanelzaz.icerock_test_task

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import ru.lzanelzaz.icerock_test_task.model.Repo
import ru.lzanelzaz.icerock_test_task.model.RepoDetails
import ru.lzanelzaz.icerock_test_task.model.UserInfo
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppRepository @Inject constructor() {

    suspend fun getRepositories(): List<Repo> {
        return NetworkModule.provideGithubApiService().getRepositories("token ${KeyValueStorage.authToken}")
    }

    suspend fun getRepository(repoId: String): RepoDetails {
        return NetworkModule.provideGithubApiService().getRepository("token ${KeyValueStorage.authToken}", repoId)
    }

    suspend fun getRepositoryReadme(ownerName: String, repositoryName: String, branchName: String): String {
        return NetworkModule.provideGithubRawUserContentService().getRepositoryReadme(ownerName, repositoryName, branchName)
    }

    suspend fun signIn(token: String) : UserInfo {
        KeyValueStorage.authToken = token
        return NetworkModule.provideGithubApiService().signIn("token ${KeyValueStorage.authToken}")
    }

}
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

interface GithubRawUserContentService {
    @GET("{ownerName}/{repositoryName}/{branchName}/README.md")
    suspend fun getRepositoryReadme(
        @Path("ownerName") ownerName: String,
        @Path("repositoryName") repositoryName: String,
        @Path("branchName") branchName: String
    ): String
}