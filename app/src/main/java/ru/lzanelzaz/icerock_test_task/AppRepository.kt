package ru.lzanelzaz.icerock_test_task

import ru.lzanelzaz.icerock_test_task.network.GitHubApi

class AppRepository {
    suspend fun getRepositories(): List<Repo> {
        return GitHubApi.retrofitService.getRepo("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")
    }

    /*suspend fun getRepository(repoId: String): RepoDetails {
        // TODO:
    }

    suspend fun getRepositoryReadme(ownerName: String, repositoryName: String, branchName: String): String {
        // TODO:
    }


    suspend fun signIn(token: String): UserInfo {
        // TODO:
    }*/

    // TODO:
}