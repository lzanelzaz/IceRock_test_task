package ru.lzanelzaz.icerock_test_task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.lzanelzaz.icerock_test_task.databinding.RepoItemBinding

class ReposListAdapter :
    RecyclerView.Adapter<ReposListAdapter.ItemViewHolder>() {

    var dataset : List<Repo> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(RepoItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = dataset.size

    fun updateItems(items: List<Repo>?) {
        dataset = items ?: emptyList()
        notifyDataSetChanged()
    }

    class ItemViewHolder(private var binding: RepoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Repo) {
            with(binding) {
                itemName.text = item.name
                itemLanguage.text = item.language
                itemDescription.text = item.description
            }
        }
    }
}