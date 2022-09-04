package ru.lzanelzaz.icerock_test_task.auth

import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.context

fun getPersonalAccessTokenHintColor(state: AuthViewModel.State) = context.resources.getColor(
    when (state) {
        is AuthViewModel.State.InvalidInput -> R.color.error
        is AuthViewModel.State.Loading -> R.color.secondary
        else -> R.color.white
    }
)