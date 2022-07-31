package ru.lzanelzaz.icerock_test_task.repositories_list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import ru.lzanelzaz.icerock_test_task.Repo
import ru.lzanelzaz.icerock_test_task.databinding.RepoItemBinding

class ReposListAdapter :
    ListAdapter<Repo, ReposListAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(RepoItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ItemViewHolder(private val binding: RepoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Repo) {
            with(binding) {
                itemName.text = item.name
                itemLanguage.text = item.language

                if (item.description == null)
                    itemDescription.visibility = View.GONE
                else
                    itemDescription.text = item.description

                itemLanguage.setLanguageColor(item.language)
            }
        }

        private fun TextView.setLanguageColor(language: String?) {
            if (language != null) {
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
    }

    class DiffCallback: DiffUtil.ItemCallback<Repo>() {
        override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
            return oldItem == newItem
        }

    }
}