package ru.lzanelzaz.icerock_test_task.ui

import android.content.Context
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.repositories_list.RepositoriesListViewModel
import ru.lzanelzaz.icerock_test_task.repository_info.RepositoryInfoViewModel

fun <T> isLoading(t: T): Boolean =
    t is RepositoriesListViewModel.State.Loading || t is RepositoryInfoViewModel.State.Loading

fun <T> isError(t: T): Boolean =
    t is RepositoriesListViewModel.State.Error
            || t is RepositoryInfoViewModel.State.Error
            || t is RepositoryInfoViewModel.ReadmeState.Error

fun <T> isEmpty(t: T): Boolean = t is RepositoriesListViewModel.State.Empty

fun <T> stateErrorMessage(t: T): String = when (t) {
    is RepositoriesListViewModel.State.Error -> t.error
    is RepositoryInfoViewModel.State.Error -> t.error
    is RepositoryInfoViewModel.ReadmeState.Error -> t.error
    else -> throw IllegalArgumentException("stateErrorMessage")
}

// State.Error, State.Empty, State.Loading -> resId
// State.Loaded -> non
fun <T> getImageResource(state: T): Int {
    return when {
        isLoading(state) -> R.drawable.loading_animation
        isError(state) -> {
            if (stateErrorMessage(state) == "Connection error")
                R.drawable.connection_error
            else R.drawable.something_error
        }
        isEmpty(state) -> R.drawable.empty_error
        else -> 0
    }
}

// State.Error, State.Empty -> String
// State.Loaded, State.Loading -> null
fun <T> getErrorText(context: Context, state: T): String? = when {
    isError(state) -> {
        if (stateErrorMessage(state) == "Connection error") context.resources.getString(R.string.connection_error)
        else context.resources.getString(R.string.something_error)
    }
    isEmpty(state) -> context.resources.getString(R.string.empty_error)
    else -> null
}

// State.Error, State.Empty -> String
// State.Loaded, State.Loading -> null
fun <T> getErrorHintText(context: Context, state: T): String? = when {
    isError(state) -> {
        if (stateErrorMessage(state) == "Connection error") context.resources.getString(
            R.string.connection_error_hint
        )
        else context.resources.getString(R.string.something_error_hint)
    }
    isEmpty(state) -> context.resources.getString(R.string.empty_error_hint)
    else -> null
}

fun <T> getErrorTextColor(context: Context, state: T): Int = context.resources.getColor(
    when {
        isError(state) -> R.color.error
        isEmpty(state) -> R.color.secondary
        else -> R.color.white
    }
)

fun <T> getRetryButtonText(context: Context, state: T): String? = when {
    isError(state) -> context.resources.getString(R.string.retry_button)
    isEmpty(state) -> context.resources.getString(R.string.refresh_button)
    else -> null
}
