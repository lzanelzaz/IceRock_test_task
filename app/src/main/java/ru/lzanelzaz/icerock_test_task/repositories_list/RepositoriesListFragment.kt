package ru.lzanelzaz.icerock_test_task.repositories_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
import dagger.hilt.android.AndroidEntryPoint
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.databinding.FragmentListRepositoriesBinding
import ru.lzanelzaz.icerock_test_task.getErrorHintText
import ru.lzanelzaz.icerock_test_task.getErrorText
import ru.lzanelzaz.icerock_test_task.getErrorTextColor
import ru.lzanelzaz.icerock_test_task.getImageResource
import ru.lzanelzaz.icerock_test_task.getRetryButtonText

private typealias Loading = RepositoriesListViewModel.State.Loading
private typealias Loaded = RepositoriesListViewModel.State.Loaded

@AndroidEntryPoint
class RepositoriesListFragment : Fragment() {
    private val viewModel: RepositoriesListViewModel by viewModels()
    lateinit var binding: FragmentListRepositoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListRepositoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindToViewModel()
    }

    private fun bindToViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.log_out -> {
                        viewModel.onLogOutButtonPressed()
                        findNavController()
                            .navigate(R.id.action_listRepositoriesFragment_to_authFragment)
                        true
                    }
                    else -> false
                }
            }
            binding.topAppBar.updateLayoutParams<AppBarLayout.LayoutParams> {
                scrollFlags = if (state is Loaded)
                    SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                else SCROLL_FLAG_NO_SCROLL
            }

            binding.listRepositoriesRecyclerView.adapter = getRecyclerViewAdapter(state)

            with(binding.stateViewLayout) {
                // Error/ loading view
                stateView.visibility =
                    if (state is Loaded) View.GONE else View.VISIBLE

                statusImageView.setImageResource(getImageResource(state))
                println(getImageResource(state))

                errorTextView.text = getErrorText(state)
                errorTextView.setTextColor(getErrorTextColor(state))

                hintTextView.text = getErrorHintText(state)

                retryButton.visibility =
                    if (state is Loading) View.GONE else View.VISIBLE
                retryButton.text = getRetryButtonText(state)

                retryButton.setOnClickListener { viewModel.onRetryButtonPressed() }
            }
        }
    }

}