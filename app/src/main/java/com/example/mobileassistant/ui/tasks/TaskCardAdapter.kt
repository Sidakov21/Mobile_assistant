//package com.example.mobileassistant.ui.tasks
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.mobileassistant.databinding.ItemTaskCardBinding
//import com.example.mobileassistant.domain.model.TaskCard
//
//class TaskCardAdapter : RecyclerView.Adapter<TaskCardAdapter.TaskCardViewHolder>() {
//
//    private val items = mutableListOf<TaskCard>()
//    var onItemClick: ((TaskCard) -> Unit)? = null
//
//    fun submitList(newItems: List<TaskCard>) {
//        items.clear()
//        items.addAll(newItems)
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskCardViewHolder {
//        val binding = ItemTaskCardBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return TaskCardViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: TaskCardViewHolder, position: Int) {
//        holder.bind(items[position])
//
//        holder.itemView.setOnClickListener {
//            onItemClick?.invoke(items[position])
//        }
//    }
//
//    override fun getItemCount(): Int = items.size
//
//    class TaskCardViewHolder(
//        private val binding: ItemTaskCardBinding
//    ) : RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: TaskCard) {
//            binding.tvTaskTitle.text = item.title
//
//            // Показываем превью заметки (первые 100 символов)
//            val notePreview = if (item.note.length > 100) {
//                "${item.note.substring(0, 100)}..."
//            } else {
//                item.note
//            }
//            binding.tvTaskNote.text = notePreview
//        }
//    }
//}