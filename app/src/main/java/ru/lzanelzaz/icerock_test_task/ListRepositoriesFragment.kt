package ru.lzanelzaz.icerock_test_task

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import ru.lzanelzaz.icerock_test_task.databinding.FragmentListRepositoriesBinding

class ListRepositoriesFragment : Fragment() {

    private val viewModel: RepositoriesListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentListRepositoriesBinding.inflate(inflater, container, false)
//listOf(Repo("repo", "descr", "Kotlin"))

        viewModel.getRepositories().observe(viewLifecycleOwner, Observer<List<Repo>> { newList ->
            binding.listRepositoriesRecyclerView.adapter = ReposListAdapter(newList)
        })

        binding.listRepositoriesRecyclerView.setHasFixedSize(true)

        return binding.root
    }
}