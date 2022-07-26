package ru.lzanelzaz.icerock_test_task

import kotlinx.serialization.Serializable

@Serializable
data class Repo (val name: String,
                 val description: String?,
                 val language: String?)