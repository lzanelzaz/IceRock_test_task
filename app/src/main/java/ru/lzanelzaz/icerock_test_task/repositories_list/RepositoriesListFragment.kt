package ru.lzanelzaz.icerock_test_task.repositories_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.*
import dagger.hilt.android.AndroidEntryPoint
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.databinding.FragmentListRepositoriesBinding

@AndroidEntryPoint
class RepositoriesListFragment : Fragment() {

    lateinit var binding: FragmentListRepositoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListRepositoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindToViewModel()

        binding.stateViewLayout.retryButton.setOnClickListener {
            bindToViewModel()
        }

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

    private fun bindToViewModel() {
        val viewModel = RepositoriesListViewModel()
        viewModel.state.observe(viewLifecycleOwner) { state ->
            // Toolbar
            binding.topAppBar.updateLayoutParams<AppBarLayout.LayoutParams> {
                scrollFlags = if (state is RepositoriesListViewModel.State.Loaded)
                    SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                else SCROLL_FLAG_NO_SCROLL
            }
            //recyclerView
            binding.listRepositoriesRecyclerView.adapter = getRecyclerViewAdapter(state)

            with(binding.stateViewLayout) {
                // Error/ loading view
                stateView.visibility =
                    if (state is RepositoriesListViewModel.State.Loaded) View.GONE else View.VISIBLE

                statusImageView.setImageResource(getImageResource(state))

                errorTextView.text = getErrorText(state)
                errorTextView.setTextColor(getErrorTextColor(state))

                hintTextView.text = getErrorHintText(state)

                retryButton.visibility =
                    if (state is RepositoriesListViewModel.State.Loading) View.GONE else View.VISIBLE
                retryButton.text = getRetryButtonText(state)
            }
        }
    }

    private fun getRecyclerViewAdapter(state: RepositoriesListViewModel.State): ReposListAdapter {
        val myRecyclerViewAdapter = ReposListAdapter()
        val dataset = if (state is RepositoriesListViewModel.State.Loaded)
            state.repos
        else
            emptyList()
        myRecyclerViewAdapter.submitList(dataset)
        return myRecyclerViewAdapter
    }

    // State.Error, State.Empty, State.Loading -> resId
    // State.Loaded -> non
    private fun getImageResource(state: RepositoriesListViewModel.State): Int = when (state) {
        is RepositoriesListViewModel.State.Loading -> R.drawable.loading_animation
        is RepositoriesListViewModel.State.Error ->
            when (state.error) {
                "java.net.UnknownHostException" -> R.drawable.connection_error
                else -> R.drawable.something_error
            }
        is RepositoriesListViewModel.State.Empty -> R.drawable.empty_error
        else -> 0
    }

    // State.Error, State.Empty -> String
    // State.Loaded, State.Loading -> null
    private fun getErrorText(state: RepositoriesListViewModel.State): String? = when (state) {
        is RepositoriesListViewModel.State.Error ->
            when (state.error) {
                "java.net.UnknownHostException" -> resources.getString(R.string.connection_error)
                else -> resources.getString(R.string.something_error)
            }
        is RepositoriesListViewModel.State.Empty -> resources.getString(R.string.empty_error)
        else -> null
    }

    // State.Error, State.Empty -> String
    // State.Loaded, State.Loading -> null
    private fun getErrorHintText(state: RepositoriesListViewModel.State): String? = when (state) {
        is RepositoriesListViewModel.State.Error ->
            when (state.error) {
                "java.net.UnknownHostException" -> resources.getString(R.string.connection_error_hint)
                else -> resources.getString(R.string.something_error_hint)
            }
        is RepositoriesListViewModel.State.Empty -> resources.getString(R.string.empty_error_hint)
        else -> null
    }

    private fun getErrorTextColor(state: RepositoriesListViewModel.State): Int = resources.getColor(when (state) {
        is RepositoriesListViewModel.State.Error -> R.color.error
        is RepositoriesListViewModel.State.Empty -> R.color.secondary
        else -> R.color.white
    }
    )

    private fun getRetryButtonText(state: RepositoriesListViewModel.State): String? = when (state) {
        is RepositoriesListViewModel.State.Error -> resources.getString(R.string.retry_button)
        is RepositoriesListViewModel.State.Empty -> resources.getString(R.string.refresh_button)
        else -> null
    }
}