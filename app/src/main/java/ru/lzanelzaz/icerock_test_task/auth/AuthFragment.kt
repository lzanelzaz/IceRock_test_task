package ru.lzanelzaz.icerock_test_task.auth

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
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.databinding.FragmentAuthBinding
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment() {

    lateinit var viewModel: AuthViewModel

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

        bindToViewModel()
    }

    private fun bindToViewModel() {
        viewModel = AuthViewModel()

        binding.editToken.doOnTextChanged { text, start, count, after ->
            binding.editToken.alpha = if (text.isNullOrEmpty()) 0.5F else 1F }

        binding.signInButton.setOnClickListener { view: View ->
            viewModel.token.value = binding.editToken.text.toString()
            viewModel.onSignButtonPressed()
            viewModel.state.observe(viewLifecycleOwner) { state ->
                with(binding) {
                    personalAccessTokenHint.visibility = View.VISIBLE
                    invalidTokenError.visibility =
                        if (state is AuthViewModel.State.InvalidInput) View.VISIBLE else View.INVISIBLE
                    personalAccessTokenHint.setTextColor(getPersonalAccessTokenHintColor(state))
                    personalAccessTokenHint.alpha = if (state is AuthViewModel.State.Loading) 0.5F else 1F
                    editToken.setBackgroundTintList(ColorStateList.valueOf(getPersonalAccessTokenHintColor(state)))
                }
                if (state is AuthViewModel.State.Idle)
                    view.findNavController()
                        .navigate(R.id.action_authFragment_to_listRepositoriesFragment)
                if (state is AuthViewModel.State.InvalidInput) {
                    binding.invalidTokenError.text = resources.getString(R.string.invalid_token)
                    binding.invalidTokenError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getPersonalAccessTokenHintColor(state: AuthViewModel.State) = resources.getColor(when (state) {
        is AuthViewModel.State.InvalidInput -> R.color.error
        is AuthViewModel.State.Loading -> R.color.white
        else -> R.color.secondary
    }
    )
}