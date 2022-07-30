package ru.lzanelzaz.icerock_test_task.auth

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.lzanelzaz.icerock_test_task.KeyValueStorage
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.databinding.FragmentAuthBinding
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment() {
    @Inject
    lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = AuthViewModel()

        val binding = FragmentAuthBinding.inflate(inflater, container, false)
        binding.invalidTokenError.visibility = View.INVISIBLE
        binding.personalAccessTokenHint.setTextColor(Color.parseColor("#B00020"))

        binding.signInButton.setOnClickListener { view: View ->
            viewModel.onSignButtonPressed()
            view.findNavController().navigate(R.id.action_authFragment_to_listRepositoriesFragment)
        }

        return binding.root
    }

}