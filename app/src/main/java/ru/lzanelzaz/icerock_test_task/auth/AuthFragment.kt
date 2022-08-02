package ru.lzanelzaz.icerock_test_task.auth

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.lzanelzaz.icerock_test_task.KeyValueStorage
import ru.lzanelzaz.icerock_test_task.MainActivity
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.databinding.FragmentAuthBinding
import ru.lzanelzaz.icerock_test_task.repositories_list.RepositoriesListViewModel
import javax.inject.Inject

typealias State = AuthViewModel.State
typealias Loading = AuthViewModel.State.Loading
typealias InvalidInput = AuthViewModel.State.InvalidInput
typealias Idle = AuthViewModel.State.Idle

@AndroidEntryPoint
class AuthFragment : Fragment() {

    lateinit var binding: FragmentAuthBinding

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
            view.findNavController()
                .navigate(R.id.action_authFragment_to_listRepositoriesFragment)
        }

        bindToViewModel()
    }

    private fun bindToViewModel() {

        binding.editToken.doOnTextChanged { text, _, _, _ ->
            binding.editToken.alpha = if (text.isNullOrEmpty()) 0.5F else 1F
        }

        binding.signInButton.setOnClickListener { view: View ->
            val viewModel = AuthViewModel()
            viewModel.token.value = binding.editToken.text.toString()
            viewModel.onSignButtonPressed()
            viewModel.state.observe(viewLifecycleOwner) { state ->
                with(binding) {
                    loadingImageView.visibility =
                        if (state is InvalidInput) View.GONE else View.VISIBLE
                    signInButton.text =
                        if (state is InvalidInput) resources.getString(R.string.sign_in_button) else null

                    personalAccessTokenHint.visibility = View.VISIBLE
                    personalAccessTokenHint.setTextColor(getPersonalAccessTokenHintColor(state))
                    personalAccessTokenHint.alpha =
                        if (state is Loading) 0.5F else 1F

                    editToken.backgroundTintList = ColorStateList.valueOf(
                        getPersonalAccessTokenHintColor(state)
                    )

                    invalidTokenError.visibility =
                        if (state is InvalidInput) View.VISIBLE else View.INVISIBLE
                    invalidTokenError.text = resources.getString(R.string.invalid_token)
                }
                if (state is Idle) {
                    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                    val editor = sharedPref?.edit()
                    editor?.putString("authToken", KeyValueStorage.authToken)
                    editor?.commit()

                    view.findNavController()
                        .navigate(R.id.action_authFragment_to_listRepositoriesFragment)
                }

            }
        }
    }

    private fun getPersonalAccessTokenHintColor(state: State) = resources.getColor(
        when (state) {
            is InvalidInput -> R.color.error
            is Loading -> R.color.white
            else -> R.color.secondary
        }
    )
}