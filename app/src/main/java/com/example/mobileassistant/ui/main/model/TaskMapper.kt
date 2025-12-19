package com.example.mobileassistant.ui.main.model

import com.example.mobileassistant.data.local.entity.TaskEntity
import com.example.mobileassistant.domain.model.Task
import com.example.mobileassistant.domain.model.TaskCardUi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TaskMapper {
    fun toDomain(entity: TaskEntity): Task {
        return Task(
            id = entity.taskId,
            title = entity.title,
            subGoalId = entity.subGoalId,
            progress = entity.progress,
            isDone = entity.isDone,
            completedAt = entity.completedAt,
            note = entity.note,
            createdAt = entity.createdAt
        )
    }

    // Domain -> Entity
    fun toEntity(domain: Task): TaskEntity {
        return TaskEntity(
            taskId = domain.id,
            subGoalId = domain.subGoalId,
            title = domain.title,
            note = domain.note,
            isDone = domain.isDone,
            progress = domain.progress,
            createdAt = domain.createdAt,
            completedAt = domain.completedAt
        )
    }

    // Domain -> UI
// Обновляем функцию toUi:
    fun toUi(
        task: Task,
        subGoalTitle: String,
        subGoalColor: Int
    ): TaskCardUi {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return TaskCardUi(
            id = task.id,
            title = task.title,
            progress = task.progress,
            note = task.note,
            subGoalTitle = subGoalTitle,
            subGoalColor = subGoalColor,
            formattedDate = dateFormat.format(Date(task.createdAt)),
            subGoalId = task.subGoalId,
            isCompleted = task.isDone,
            completedAt = task.completedAt,
            createdAt = task.createdAt
        )
    }

    // Entity -> UI (прямой маппинг)
    fun toUi(
        entity: TaskEntity,
        subGoalTitle: String,
        subGoalColor: Int
    ): TaskCardUi {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return TaskCardUi(
            id = entity.taskId,
            title = entity.title,
            progress = entity.progress,
            note = entity.note,
            subGoalTitle = subGoalTitle,
            subGoalColor = subGoalColor,
            formattedDate = dateFormat.format(Date(entity.createdAt)),
            subGoalId = entity.subGoalId,
            createdAt = entity.createdAt
        )
    }


}