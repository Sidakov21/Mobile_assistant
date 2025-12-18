package com.example.mobileassistant.ui.main.model

import com.example.mobileassistant.domain.model.Task
import com.example.mobileassistant.domain.model.TaskCardUi

fun TaskUi.toDomain(): Task {
    return Task(
        id = this.id,
        title = this.title,
        progress = this.progress,
        isDone = false,
        completedAt = null,
        note = ""
    )
}

// Дополнительный маппер для TaskCardUi, если нужно
fun Task.toTaskCardUi(subGoalTitle: String = "", subGoalColor: Int = 0xFF4CAF50.toInt()): TaskCardUi {
    return TaskCardUi(
        id = this.id,
        title = this.title,
        progress = this.progress,
        note = this.note,
        subGoalTitle = subGoalTitle,
        subGoalColor = subGoalColor,
        formattedDate = "" // Форматируется в ViewModel
    )
}