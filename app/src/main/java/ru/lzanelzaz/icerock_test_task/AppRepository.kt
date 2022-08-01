package ru.lzanelzaz.icerock_test_task

import ru.lzanelzaz.icerock_test_task.network.GitHubApi

class AppRepository {
    suspend fun getRepositories(): List<Repo> {
        return GitHubApi.retrofitService.getRepositories()
    }

    suspend fun getRepository(repoId: String): RepoDetails {
        return GitHubApi.retrofitService.getRepository(repoId)
    }

    suspend fun getRepositoryReadme(ownerName: String, repositoryName: String, branchName: String): String {
        return GitHubApi.retrofitService.getRepositoryReadme(ownerName, repositoryName, branchName)
    }

    suspend fun signIn(token: String) : UserInfo {
        KeyValueStorage.authToken = token
        return GitHubApi.retrofitService.signIn("token ${KeyValueStorage.authToken}")
    }
}