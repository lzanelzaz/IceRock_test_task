package ru.lzanelzaz.icerock_test_task

import android.content.res.Resources
import android.graphics.Color
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

        viewModel.state.observe(viewLifecycleOwner, Observer<RepositoriesListViewModel.State> { state ->
            with(binding) {
                when (state) {
                    RepositoriesListViewModel.State.LOADING -> {
                        statusView.visibility = View.VISIBLE
                        statusImageView.setImageResource(R.drawable.loading_animation)
                    }
                    RepositoriesListViewModel.State.ERROR -> {
                        statusView.visibility = View.VISIBLE
                        statusImageView.setImageResource(R.drawable.connection_error)
                        errorTextView.setText(R.string.connection_error)
                        errorTextView.setTextColor(resources.getColor(R.color.error, null))
                        hintTextView.setText(R.string.connection_error_hint)
                    }
                    RepositoriesListViewModel.State.LOADED -> statusView.visibility = View.GONE
                }
            }
        })


        viewModel.getRepositories().observe(viewLifecycleOwner, Observer<List<Repo>> { newList ->
            binding.listRepositoriesRecyclerView.adapter = ReposListAdapter(newList)
        })

        binding.listRepositoriesRecyclerView.setHasFixedSize(true)

        return binding.root
    }
}