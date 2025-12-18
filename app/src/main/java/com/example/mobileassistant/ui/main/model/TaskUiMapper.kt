package com.example.mobileassistant.ui.main.model

import com.example.mobileassistant.domain.model.Task
import com.example.mobileassistant.domain.model.TaskCardUi
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun TaskUi.toDomain(): Task {
    return Task(
        id = this.id,
        title = this.title,
        subGoalId = this.subGoalId,
        progress = this.progress,
        isDone = false,
        completedAt = null,
        note = ""
    )
}

fun Task.toTaskCardUi(
    subGoalTitle: String = "",
    subGoalColor: Int = 0xFF4CAF50.toInt(),
    subGoalId: Int = 0
): TaskCardUi {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val date = dateFormat.format(Date(this.createdAt))

    return TaskCardUi(
        id = this.id,
        title = this.title,
        progress = this.progress,
        note = this.note,
        subGoalTitle = subGoalTitle,
        subGoalColor = subGoalColor,
        formattedDate = date,
        subGoalId = subGoalId // Используем переданный subGoalId
    )
}