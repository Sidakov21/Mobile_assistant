package com.example.mobileassistant.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileassistant.databinding.ItemSubgoalCardBinding
import com.example.mobileassistant.domain.model.TaskCard

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val items = mutableListOf<TaskCard>()

    var onItemClick: ((TaskCard) -> Unit)? = null

    fun submitList(newItems: List<TaskCard>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemSubgoalCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(items[position])

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    class TaskViewHolder(
        private val binding: ItemSubgoalCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TaskCard) {
            binding.tvTaskTitle.text = item.title
            binding.tvTaskNote.text = item.note
        }
    }
}