package ru.lzanelzaz.icerock_test_task.repositories_list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import ru.lzanelzaz.icerock_test_task.R
import ru.lzanelzaz.icerock_test_task.model.Repo
import ru.lzanelzaz.icerock_test_task.databinding.RepoItemBinding
import ru.lzanelzaz.icerock_test_task.repository_info.RepositoryInfoFragment

class ReposListAdapter :
    ListAdapter<Repo, ReposListAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(RepoItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener { view ->
            view.findNavController()
                .navigate(
                    R.id.action_listRepositoriesFragment_to_repositoryInfoFragment,
                    RepositoryInfoFragment.createArguments(repoName = item.name)
                )
        }
    }

    class ItemViewHolder(private val binding: RepoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Repo) {
            with(binding) {
                itemName.text = item.name

                itemLanguage.text = item.language
                itemLanguage.setLanguageColor(item.language)

                itemDescription.text = item.description
                itemDescription.visibility =
                    if (item.description == null) View.GONE else View.VISIBLE
            }
        }

        private fun TextView.setLanguageColor(language: String?) {
            if (language != null) return
            // Parse json (language objects)
            val fileContent: String = context.assets
                .open("values/github_lang_colors.json")
                .bufferedReader().use { it.readText() }

            val languageColor: String = JSONObject(fileContent)
                .getJSONObject(language).optString("color")
                // white color in case of null language color
                .replace("null", "#FFFFFFFF")

            setTextColor(Color.parseColor(languageColor))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Repo>() {
        override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
            return oldItem == newItem
        }
    }
}