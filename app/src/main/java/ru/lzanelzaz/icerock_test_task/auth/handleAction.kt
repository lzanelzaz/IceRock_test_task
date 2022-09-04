package ru.lzanelzaz.icerock_test_task.auth

import android.app.AlertDialog
import android.view.View
import androidx.navigation.findNavController
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.context

fun handleAction(view: View, action: AuthViewModel.Action) {
    when (action) {
        is AuthViewModel.Action.ShowError -> {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(
                if (action.message == "Error data") context.resources.getString(R.string.error_dialog_title)
                else context.resources.getString(R.string.connection_error)
            ).setMessage(
                if (action.message == "Error data") context.resources.getString(R.string.error_dialog_message)
                else context.resources.getString(R.string.connection_error_hint)
            ).setPositiveButton(context.resources.getString(R.string.ok)) { dialog, _ ->
                dialog.cancel()
            }
            val dialog = builder.create()
            dialog.show()
        }
        is AuthViewModel.Action.RouteToMain -> {
            view.findNavController()
                .navigate(R.id.action_authFragment_to_listRepositoriesFragment)
        }
    }
}