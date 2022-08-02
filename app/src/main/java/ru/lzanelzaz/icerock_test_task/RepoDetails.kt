package ru.lzanelzaz.icerock_test_task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDetails(
    val name: String,
    @SerialName("html_url")
    val link: String,
    val license: License?,
    val forks: Int,
    @SerialName("stargazers_count")
    val stars: Int,
    @SerialName("subscribers_count")
    val watchers: Int,
    val owner: Owner,
    @SerialName("default_branch")
    val defaultBranch: String
)

@Serializable
data class License(
    @SerialName("spdx_id")
    val id: String
)

@Serializable
data class Owner(
    val login: String
)