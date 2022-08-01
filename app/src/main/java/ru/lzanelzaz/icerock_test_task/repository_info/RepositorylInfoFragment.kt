package ru.lzanelzaz.icerock_test_task.repository_info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.RepoDetails
import ru.lzanelzaz.icerock_test_task.databinding.FragmentRepositoryInfoBinding
import ru.lzanelzaz.icerock_test_task.repositories_list.ReposListAdapter
import ru.lzanelzaz.icerock_test_task.repositories_list.RepositoriesListViewModel

typealias State = RepositoryInfoViewModel.State
typealias Loading = RepositoryInfoViewModel.State.Loading
typealias Loaded = RepositoryInfoViewModel.State.Loaded
typealias Error = RepositoryInfoViewModel.State.Error

class RepositorylInfoFragment : Fragment() {

    lateinit var viewModel: RepositoryInfoViewModel
    lateinit var binding: FragmentRepositoryInfoBinding
    lateinit var repoId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        repoId = requireArguments().getString(REPO_ID).let { requireNotNull(it) }
        binding = FragmentRepositoryInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindToViewModel()

        binding.stateViewLayout.retryButton.setOnClickListener {
            bindToViewModel()
        }
        binding.topAppBar.setNavigationOnClickListener {
            it.findNavController()
                .navigate(R.id.action_repositorylInfoFragment_to_listRepositoriesFragment)
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.log_out -> {
                    view.findNavController()
                        .navigate(R.id.action_repositorylInfoFragment_to_authFragment)
                    true
                }
                else -> false
            }
        }
    }

    companion object {
        private const val REPO_ID = "repoId"

        fun createArguments(repoId: String): Bundle {
            return bundleOf(REPO_ID to repoId)
        }
    }

    private fun bindToViewModel() {
        viewModel = RepositoryInfoViewModel(repoId)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            with(binding) {
                topAppBar.title = repoId
                repositoryView.visibility =
                    if (state is Loaded) View.VISIBLE else View.INVISIBLE
                val githubRepo: RepoDetails? =
                    if (state is Loaded) state.githubRepo else null

                linkTextView.text = githubRepo?.link?.drop("https://".length)
                licenseTextView.text = githubRepo?.license?.id
                starsCount.text = githubRepo?.stars.toString()
                forksCount.text = githubRepo?.forks.toString()
                watchersCount.text = githubRepo?.watchers.toString()
            }

            with(binding.stateViewLayout) {
                // Error/ loading view
                stateView.visibility =
                    if (state is Loaded) View.GONE else View.VISIBLE

                statusImageView.setImageResource(getImageResource(state))

                errorTextView.text = getErrorText(state)
                hintTextView.text = getErrorHintText(state)

                retryButton.visibility =
                    if (state is RepositoryInfoViewModel.State.Loading) View.GONE else View.VISIBLE
            }
        }
    }


    // State.Error, State.Empty, State.Loading -> resId
    // State.Loaded -> non
    private fun getImageResource(state: State): Int = when (state) {
        is Loading -> R.drawable.loading_animation
        is Error ->
            when (state.error) {
                "java.net.UnknownHostException" -> R.drawable.connection_error
                else -> R.drawable.something_error
            }
        else -> 0
    }

    // State.Error, State.Empty -> String
    // State.Loaded, State.Loading -> null
    private fun getErrorText(state: State): String? = when (state) {
        is Error ->
            when (state.error) {
                "java.net.UnknownHostException" -> resources.getString(R.string.connection_error)
                else -> resources.getString(R.string.something_error)
            }
        else -> null
    }

    // State.Error, State.Empty -> String
    // State.Loaded, State.Loading -> null
    private fun getErrorHintText(state: State): String? = when (state) {
        is Error ->
            when (state.error) {
                "java.net.UnknownHostException" -> resources.getString(R.string.connection_error_hint)
                else -> resources.getString(R.string.something_error_hint)
            }
        else -> null
    }

}