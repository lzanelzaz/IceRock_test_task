package ru.lzanelzaz.icerock_test_task

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import ru.lzanelzaz.icerock_test_task.databinding.RepoItemBinding

class ReposListAdapter(private val dataset: List<Repo>) :
    RecyclerView.Adapter<ReposListAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(RepoItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = dataset.size

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
}