package ru.lzanelzaz.icerock_test_task.auth

import android.content.Context
import ru.lzanelzaz.icerock_test_task.R

fun getPersonalAccessTokenHintColor(context: Context, state: AuthViewModel.State) = context.resources.getColor(
    when (state) {
        is AuthViewModel.State.InvalidInput -> R.color.error
        is AuthViewModel.State.Loading -> R.color.secondary
        else -> R.color.white
    }
)