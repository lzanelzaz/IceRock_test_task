package ru.lzanelzaz.icerock_test_task

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.lzanelzaz.icerock_test_task.databinding.FragmentListRepositoriesBinding
import javax.inject.Inject

@AndroidEntryPoint
class ListRepositoriesFragment : Fragment() {

    lateinit var viewModel: RepositoriesListViewModel

    lateinit var binding : FragmentListRepositoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = RepositoriesListViewModel()
        binding = FragmentListRepositoriesBinding.inflate(inflater, container, false)

//listOf(Repo("repo", "descr", "Kotlin"))

        viewModel.state.observe(
            viewLifecycleOwner,
            Observer<RepositoriesListViewModel.State> { state ->
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
                            errorTextView.setTextColor(resources.getColor(R.color.error))
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.topAppBar.setNavigationOnClickListener {
            it.findNavController().navigate(R.id.action_listRepositoriesFragment_to_authFragment)
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.log_out -> {
                    view.findNavController().navigate(R.id.action_listRepositoriesFragment_to_authFragment)
                    true
                }
                else -> false
            }

        }
    }
}