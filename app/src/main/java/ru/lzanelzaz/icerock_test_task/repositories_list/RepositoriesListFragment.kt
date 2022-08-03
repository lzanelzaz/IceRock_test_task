package ru.lzanelzaz.icerock_test_task.repositories_list

import android.content.Context
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
import ru.lzanelzaz.icerock_test_task.KeyValueStorage
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.databinding.FragmentListRepositoriesBinding

typealias State = RepositoriesListViewModel.State
typealias Loading = RepositoriesListViewModel.State.Loading
typealias Loaded = RepositoriesListViewModel.State.Loaded
typealias Error = RepositoriesListViewModel.State.Error
typealias Empty = RepositoriesListViewModel.State.Empty

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
                    val sharedPref = activity?.getSharedPreferences("USER_API_TOKEN", Context.MODE_PRIVATE)
                    val editor = sharedPref?.edit()
                    editor?.clear()
                    editor?.commit()
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
        viewModel.getState().observe(viewLifecycleOwner) { state ->
            // Toolbar
            binding.topAppBar.updateLayoutParams<AppBarLayout.LayoutParams> {
                scrollFlags = if (state is Loaded)
                    SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                else SCROLL_FLAG_NO_SCROLL
            }
            //recyclerView
            binding.listRepositoriesRecyclerView.adapter = getRecyclerViewAdapter(state)

            with(binding.stateViewLayout) {
                // Error/ loading view
                stateView.visibility =
                    if (state is Loaded) View.GONE else View.VISIBLE

                statusImageView.setImageResource(getImageResource(state))

                errorTextView.text = getErrorText(state)
                errorTextView.setTextColor(getErrorTextColor(state))

                hintTextView.text = getErrorHintText(state)

                retryButton.visibility =
                    if (state is Loading) View.GONE else View.VISIBLE
                retryButton.text = getRetryButtonText(state)
            }
        }
    }

    private fun getRecyclerViewAdapter(state: State): ReposListAdapter {
        val myRecyclerViewAdapter = ReposListAdapter()
        val dataset = if (state is Loaded)
            state.repos
        else
            emptyList()
        myRecyclerViewAdapter.submitList(dataset)
        return myRecyclerViewAdapter
    }

    // State.Error, State.Empty, State.Loading -> resId
    // State.Loaded -> non
    private fun getImageResource(state: State): Int = when (state) {
        is Loading -> R.drawable.loading_animation
        is Error ->
            when (state.error) {
                "Connection error" -> R.drawable.connection_error
                else -> R.drawable.something_error
            }
        is Empty -> R.drawable.empty_error
        else -> 0
    }

    // State.Error, State.Empty -> String
    // State.Loaded, State.Loading -> null
    private fun getErrorText(state: State): String? = when (state) {
        is Error ->
            when (state.error) {
                "Connection error" -> resources.getString(R.string.connection_error)
                else -> resources.getString(R.string.something_error)
            }
        is Empty -> resources.getString(R.string.empty_error)
        else -> null
    }

    // State.Error, State.Empty -> String
    // State.Loaded, State.Loading -> null
    private fun getErrorHintText(state: State): String? = when (state) {
        is Error ->
            when (state.error) {
                "Connection error" -> resources.getString(R.string.connection_error_hint)
                else -> resources.getString(R.string.something_error_hint)
            }
        is Empty -> resources.getString(R.string.empty_error_hint)
        else -> null
    }

    private fun getErrorTextColor(state: State): Int = resources.getColor(when (state) {
        is Error -> R.color.error
        is Empty -> R.color.secondary
        else -> R.color.white
    }
    )

    private fun getRetryButtonText(state: State): String? = when (state) {
        is Error -> resources.getString(R.string.retry_button)
        is Empty -> resources.getString(R.string.refresh_button)
        else -> null
    }
}