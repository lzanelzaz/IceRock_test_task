package ru.lzanelzaz.icerock_test_task.repository_info

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.view.updateLayoutParams
import androidx.navigation.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.*
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.RepoDetails
import ru.lzanelzaz.icerock_test_task.databinding.FragmentRepositoryInfoBinding

typealias State = RepositoryInfoViewModel.State
typealias Loading = RepositoryInfoViewModel.State.Loading
typealias Loaded = RepositoryInfoViewModel.State.Loaded
typealias Error = RepositoryInfoViewModel.State.Error

typealias ReadmeState = RepositoryInfoViewModel.ReadmeState
typealias ReadmeLoading = RepositoryInfoViewModel.ReadmeState.Loading
typealias ReadmeLoaded = RepositoryInfoViewModel.ReadmeState.Loaded
typealias ReadmeError = RepositoryInfoViewModel.ReadmeState.Error
typealias ReadmeEmpty = RepositoryInfoViewModel.ReadmeState.Empty

class RepositorylInfoFragment : Fragment() {

    lateinit var binding: FragmentRepositoryInfoBinding
    lateinit var repoId: String
    lateinit var viewModel: RepositoryInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepositoryInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        repoId = requireArguments().getString(REPO_ID).let { requireNotNull(it) }

        bindToViewModel()

        binding.topAppBar.setNavigationOnClickListener {
            it.findNavController()
                .navigate(R.id.action_repositorylInfoFragment_to_listRepositoriesFragment)
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.log_out -> {
                    val sharedPref =
                        activity?.getSharedPreferences("USER_API_TOKEN", Context.MODE_PRIVATE)
                    val editor = sharedPref?.edit()
                    editor?.clear()
                    editor?.commit()
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

        viewModel.getState().observe(viewLifecycleOwner) { state ->

            binding.topAppBar.title = repoId
            binding.topAppBar.updateLayoutParams<AppBarLayout.LayoutParams> {
                scrollFlags = if (state is Loaded && state.readmeState is ReadmeLoaded)
                    SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                else SCROLL_FLAG_NO_SCROLL
            }

            binding.repositoryView.visibility =
                if (state is Loaded) View.VISIBLE else View.INVISIBLE

            val githubRepo: RepoDetails? =
                if (state is Loaded) state.githubRepo else null

            with(binding) {
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
                    if (state is Loading) View.GONE else View.VISIBLE

                retryButton.setOnClickListener { bindToViewModel() }
            }
            loadReadme(state)
        }
    }

    private fun loadReadme(state: State) {
        if (state is Loaded) {
            val readmeState = state.readmeState
            binding.readmeTextView.text = when (readmeState) {
                is ReadmeLoaded -> {
                    val markdown = readmeState.markdown
                    // delete badges
                    val src = markdown.drop(markdown.indexOf('#'))
                    val flavour = CommonMarkFlavourDescriptor()
                    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(src)
                    val html = HtmlGenerator(src, parsedTree, flavour).generateHtml()
                    HtmlCompat.fromHtml(html, FROM_HTML_MODE_LEGACY)
                }
                is ReadmeEmpty -> resources.getString(R.string.no_readme)
                else -> null
            }

            with(binding.stateViewLayout) {
                // Error/ loading view
                stateView.visibility =
                    if (readmeState is ReadmeLoaded || readmeState is ReadmeEmpty) View.GONE else View.VISIBLE

                statusImageView.setImageResource(getImageResource(readmeState))

                errorTextView.text = getErrorText(readmeState)
                hintTextView.text = getErrorHintText(readmeState)

                retryButton.visibility =
                    if (readmeState is ReadmeError) View.VISIBLE else View.GONE

                retryButton.setOnClickListener {
                    viewModel.loadReadmeState(state.githubRepo)
                }
            }
        }
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
        else -> null
    }


    private fun getImageResource(state: ReadmeState): Int = when (state) {
        is ReadmeLoading -> R.drawable.loading_animation
        is ReadmeError ->
            when (state.error) {
                "Connection error" -> R.drawable.connection_error
                else -> R.drawable.something_error
            }
        else -> 0
    }

    // State.Error, State.Empty -> String
// State.Loaded, State.Loading -> null
    private fun getErrorText(state: ReadmeState): String? = when (state) {
        is ReadmeError ->
            when (state.error) {
                "Connection error" -> resources.getString(R.string.connection_error)
                else -> resources.getString(R.string.something_error)
            }
        else -> null
    }

    // State.Error, State.Empty -> String
// State.Loaded, State.Loading -> null
    private fun getErrorHintText(state: ReadmeState): String? = when (state) {
        is ReadmeError ->
            when (state.error) {
                "Connection error" -> resources.getString(R.string.connection_error_hint)
                else -> resources.getString(R.string.something_error_hint)
            }
        else -> null
    }

}