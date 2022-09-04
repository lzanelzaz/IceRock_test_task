package ru.lzanelzaz.icerock_test_task.auth

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.databinding.FragmentAuthBinding

private typealias Loading = AuthViewModel.State.Loading
private typealias InvalidInput = AuthViewModel.State.InvalidInput

@AndroidEntryPoint
class AuthFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.actions.collect { handleAction(view, it) }
        }
        bindToViewModel()
    }

    private fun bindToViewModel() {
        binding.editToken.doOnTextChanged { text, _, _, _ ->
            binding.editToken.alpha = if (text.isNullOrEmpty()) 0.5F else 1F
        }
        binding.signInButton.setOnClickListener {
            viewModel.onSignButtonPressed(binding.editToken.text.toString())

            viewModel.state.observe(viewLifecycleOwner) { state ->
                with(binding) {
                    loadingImageView.visibility =
                        if (state is Loading) View.VISIBLE else View.GONE
                    signInButton.text =
                        if (state is Loading) null else resources.getString(R.string.sign_in_button)

                    personalAccessTokenHint.visibility = View.VISIBLE
                    personalAccessTokenHint.setTextColor(getPersonalAccessTokenHintColor(requireContext(), state))
                    personalAccessTokenHint.alpha =
                        if (state is Loading) 0.5F else 1F

                    editToken.backgroundTintList = ColorStateList.valueOf(
                        getPersonalAccessTokenHintColor(requireContext(), state)
                    )

                    invalidTokenError.visibility =
                        if (state is InvalidInput) View.VISIBLE else View.INVISIBLE
                }
            }
        }
    }
}