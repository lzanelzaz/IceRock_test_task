package ru.lzanelzaz.icerock_test_task

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.lzanelzaz.icerock_test_task.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAuthBinding.inflate(inflater, container, false)
        binding.invalidTokenError.visibility = View.INVISIBLE
        binding.personalAccessTokenHint.setTextColor(Color.parseColor("#B00020"))
        return binding.root
    }

}