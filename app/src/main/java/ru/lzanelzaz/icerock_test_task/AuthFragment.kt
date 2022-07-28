package ru.lzanelzaz.icerock_test_task

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import ru.lzanelzaz.icerock_test_task.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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