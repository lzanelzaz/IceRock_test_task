package ru.lzanelzaz.icerock_test_task.repository_info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.lzanelzaz.icerock_test_task.R

class RepositorylInfoFragment : Fragment() {

    lateinit var viewModel: RepositoryInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_repository_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//
//        binding.topAppBar.setNavigationOnClickListener {
//            it.findNavController().navigate(R.id.action_listRepositoriesFragment_to_authFragment)
//        }
//
//        binding.topAppBar.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.log_out -> {
//                    view.findNavController().navigate(R.id.action_listRepositoriesFragment_to_authFragment)
//                    true
//                }
//                else -> false
//            }
//
//        }
    }

}