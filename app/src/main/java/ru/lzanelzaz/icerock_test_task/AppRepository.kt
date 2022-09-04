package ru.lzanelzaz.icerock_test_task

import ru.lzanelzaz.icerock_test_task.api.GithubApiService
import ru.lzanelzaz.icerock_test_task.api.GithubRawUserContentService
import ru.lzanelzaz.icerock_test_task.model.Repo
import ru.lzanelzaz.icerock_test_task.model.RepoDetails
import ru.lzanelzaz.icerock_test_task.model.UserInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val githubApiService: GithubApiService,
    private val githubRawUserContentService: GithubRawUserContentService,
    private val keyValueStorage: KeyValueStorage
) {
    fun isSignedIn(): Boolean = keyValueStorage.authToken != null

    fun logOut() {
        keyValueStorage.logOut()
    }

    suspend fun signIn(token: String): UserInfo {
        keyValueStorage.authToken = token
        return githubApiService.signIn("token ${keyValueStorage.authToken}")
    }

    suspend fun getRepositories(): List<Repo> {
        return githubApiService.getRepositories("token ${keyValueStorage.authToken}")
    }

    suspend fun getRepository(repoId: String): RepoDetails {
        return githubApiService.getRepository(
            token = "token ${keyValueStorage.authToken}",
            repoId = repoId
        )
    }

    suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String {
        return githubRawUserContentService.getRepositoryReadme(
            ownerName = ownerName, repositoryName = repositoryName, branchName = branchName
        )
    }
}