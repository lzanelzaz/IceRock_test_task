package ru.lzanelzaz.icerock_test_task.repositories_list

import android.os.Bundle
import android.view.*
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.*
import dagger.hilt.android.AndroidEntryPoint
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.databinding.FragmentListRepositoriesBinding
import javax.inject.Inject

@AndroidEntryPoint
class RepositoriesListFragment : Fragment() {

    lateinit var viewModel: RepositoriesListViewModel

    lateinit var binding: FragmentListRepositoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = RepositoriesListViewModel()
        binding = FragmentListRepositoriesBinding.inflate(inflater, container, false)

        updateState()

        binding.retryButton.setOnClickListener {
            clearView()
            updateState()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.log_out -> {
                    view.findNavController()
                        .navigate(R.id.action_listRepositoriesFragment_to_authFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun updateState() {
        viewModel.state.observe(
            viewLifecycleOwner
        ) { state ->
            with(binding) {

                topAppBar.updateLayoutParams<AppBarLayout.LayoutParams> {
                    scrollFlags = SCROLL_FLAG_NO_SCROLL
                }

                when (state) {
                    is RepositoriesListViewModel.State.Loading -> {
                        statusView.visibility = View.VISIBLE
                        statusImageView.setImageResource(R.drawable.loading_animation)
                    }

                    is RepositoriesListViewModel.State.Error -> {
                        statusView.visibility = View.VISIBLE
                        errorTextView.setTextColor(resources.getColor(R.color.error))
                        retryButton.text = resources.getString(R.string.retry_button)

                        when (state.error) {
                            "Something error" -> {
                                statusImageView.setImageResource(R.drawable.something_error)
                                errorTextView.setText(R.string.something_error)
                                hintTextView.setText(R.string.something_error_hint)
                            }
                            "Connection error" -> {
                                statusImageView.setImageResource(R.drawable.connection_error)
                                errorTextView.setText(R.string.connection_error)
                                hintTextView.setText(R.string.connection_error_hint)
                            }
                        }
                    }

                    is RepositoriesListViewModel.State.Loaded -> {
                        statusView.visibility = View.GONE
                        listRepositoriesRecyclerView.adapter = ReposListAdapter(state.repos)
                        binding.listRepositoriesRecyclerView.setHasFixedSize(true)

                        topAppBar.updateLayoutParams<AppBarLayout.LayoutParams> {
                            scrollFlags = SCROLL_FLAG_SCROLL or
                                    SCROLL_FLAG_ENTER_ALWAYS or
                                    SCROLL_FLAG_SNAP
                        }
                    }

                    is RepositoriesListViewModel.State.Empty -> {
                        statusView.visibility = View.VISIBLE

                        statusImageView.setImageResource(R.drawable.empty_error)

                        errorTextView.setText(R.string.empty_error)
                        errorTextView.setTextColor(resources.getColor(R.color.secondary))
                        hintTextView.setText(R.string.empty_hint)

                        retryButton.text = resources.getString(R.string.refresh_button)
                    }
                }
            }
        }
    }

    private fun clearView() {
        viewModel = RepositoriesListViewModel()
        with(binding) {
            statusView.visibility = View.GONE
            errorTextView.text = null
            hintTextView.text = null
        }
    }
}