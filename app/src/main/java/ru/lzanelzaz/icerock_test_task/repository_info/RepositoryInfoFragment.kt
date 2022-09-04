package ru.lzanelzaz.icerock_test_task.repository_info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.*
import dagger.hilt.android.AndroidEntryPoint
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.model.RepoDetails
import ru.lzanelzaz.icerock_test_task.databinding.FragmentRepositoryInfoBinding
import ru.lzanelzaz.icerock_test_task.getErrorHintText
import ru.lzanelzaz.icerock_test_task.getErrorText
import ru.lzanelzaz.icerock_test_task.getImageResource

typealias State = RepositoryInfoViewModel.State
typealias Loading = RepositoryInfoViewModel.State.Loading
typealias Loaded = RepositoryInfoViewModel.State.Loaded

typealias ReadmeLoading = RepositoryInfoViewModel.ReadmeState.Loading
typealias ReadmeLoaded = RepositoryInfoViewModel.ReadmeState.Loaded
typealias ReadmeError = RepositoryInfoViewModel.ReadmeState.Error
typealias ReadmeEmpty = RepositoryInfoViewModel.ReadmeState.Empty

@AndroidEntryPoint
class RepositoryInfoFragment : Fragment() {
    private val viewModel: RepositoryInfoViewModel by viewModels()
    lateinit var binding: FragmentRepositoryInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRepositoryInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindToViewModel()
    }

    private fun bindToViewModel() {
        val repoId = requireNotNull(requireArguments().getString(REPO_NAME_ARG_KEY))
        viewModel.repoId = repoId
        viewModel.state.observe(viewLifecycleOwner) { state ->
            with(binding.topAppBar) {
                title = repoId
                updateLayoutParams<AppBarLayout.LayoutParams> {
                    scrollFlags = if (state is Loaded && state.readmeState is ReadmeLoaded)
                        SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                    else SCROLL_FLAG_NO_SCROLL
                }
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.log_out -> {
                            viewModel.onLogOutButtonPressed()
                            findNavController()
                                .navigate(R.id.action_repositoryInfoFragment_to_authFragment)
                            true
                        }
                        else -> false
                    }
                }
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

                retryButton.setOnClickListener { viewModel.onRetryButtonPressed() }
            }
            loadReadme(state)
        }
    }

    private fun loadReadme(state: State) {
        if (state !is Loaded) return
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
            statusImageView.visibility =
                if (readmeState is ReadmeError) View.VISIBLE else View.GONE

            statusImageView.setImageResource(getImageResource(readmeState))

            errorTextView.text = getErrorText(readmeState)
            hintTextView.text = getErrorHintText(readmeState)

            retryButton.visibility =
                if (readmeState is ReadmeError) View.VISIBLE else View.GONE

            retryButton.setOnClickListener {
                viewModel.onRetryButtonPressed()
            }
        }
        binding.loadingImageView.visibility =
            if (readmeState is ReadmeLoading) View.VISIBLE else View.GONE

    }

    companion object {
        private const val REPO_NAME_ARG_KEY = "repoId"

        fun createArguments(repoName: String): Bundle {
            return bundleOf(REPO_NAME_ARG_KEY to repoName)
        }
    }
}