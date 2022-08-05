package ru.lzanelzaz.icerock_test_task.auth

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.KeyValueStorage
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.databinding.FragmentAuthBinding
import javax.inject.Inject

typealias State = AuthViewModel.State
typealias Loading = AuthViewModel.State.Loading
typealias InvalidInput = AuthViewModel.State.InvalidInput

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = activity?.getSharedPreferences("USER_API_TOKEN", Context.MODE_PRIVATE)
        if (sharedPref?.getString("authToken", null) != null) {
            KeyValueStorage.authToken = sharedPref.getString("authToken", null)
            findNavController()
                .navigate(R.id.action_authFragment_to_listRepositoriesFragment)
        }

        bindToViewModel()
    }

    private fun bindToViewModel() {

        binding.editToken.doOnTextChanged { text, _, _, _ ->
            binding.editToken.alpha = if (text.isNullOrEmpty()) 0.5F else 1F
        }
        binding.signInButton.setOnClickListener {
            viewModel.setToken(binding.editToken.text.toString())
            viewModel.onSignButtonPressed()

            lifecycleScope.launch {
                viewModel.actions.collect { handleAction(it) }
            }

            viewModel.state.observe(viewLifecycleOwner) { state ->
                with(binding) {
                    loadingImageView.visibility =
                        if (state is Loading) View.VISIBLE else View.GONE
                    signInButton.text =
                        if (state is Loading) null else resources.getString(R.string.sign_in_button)

                    personalAccessTokenHint.visibility = View.VISIBLE
                    personalAccessTokenHint.setTextColor(getPersonalAccessTokenHintColor(state))
                    personalAccessTokenHint.alpha =
                        if (state is Loading) 0.5F else 1F

                    editToken.backgroundTintList = ColorStateList.valueOf(
                        getPersonalAccessTokenHintColor(state)
                    )

                    invalidTokenError.visibility =
                        if (state is InvalidInput) View.VISIBLE else View.INVISIBLE
                }
            }
        }
    }

    private fun getPersonalAccessTokenHintColor(state: State) = resources.getColor(
        when (state) {
            is InvalidInput -> R.color.error
            is Loading -> R.color.secondary
            else -> R.color.white
        }
    )

    private fun handleAction(action: AuthViewModel.Action) {
        when (action) {
            is AuthViewModel.Action.ShowError -> {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(
                    if (action.message == "Error data") resources.getString(R.string.error_dialog_title)
                    else resources.getString(R.string.connection_error)
                )
                    .setMessage(
                        if (action.message == "Error data") resources.getString(R.string.error_dialog_message)
                        else resources.getString(R.string.connection_error_hint)
                    )
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                        dialog.cancel()
                    }
                val dialog = builder.create()
                dialog.show()
            }
            is AuthViewModel.Action.RouteToMain -> {
                val sharedPref =
                    activity?.getSharedPreferences("USER_API_TOKEN", Context.MODE_PRIVATE)
                val editor = sharedPref?.edit()
                editor?.putString("authToken", KeyValueStorage.authToken)
                editor?.commit()
                findNavController()
                    .navigate(R.id.action_authFragment_to_listRepositoriesFragment)
            }
        }
    }
}