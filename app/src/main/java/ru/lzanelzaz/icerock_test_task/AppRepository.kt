package ru.lzanelzaz.icerock_test_task

import ru.lzanelzaz.icerock_test_task.network.GithubApi
import ru.lzanelzaz.icerock_test_task.network.GithubRawUserContent

class AppRepository {
    suspend fun getRepositories(): List<Repo> {
        return GithubApi.retrofitService.getRepositories("token ${KeyValueStorage.authToken}")
    }

    suspend fun getRepository(repoId: String): RepoDetails {
        return GithubApi.retrofitService.getRepository("token ${KeyValueStorage.authToken}", repoId)
    }

    suspend fun getRepositoryReadme(ownerName: String, repositoryName: String, branchName: String): String {
        return GithubRawUserContent.retrofitService.getRepositoryReadme(ownerName, repositoryName, branchName)
    }

    suspend fun signIn(token: String) : UserInfo {
        KeyValueStorage.authToken = token
        return GithubApi.retrofitService.signIn("token ${KeyValueStorage.authToken}")
    }
}