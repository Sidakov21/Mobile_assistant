package com.example.mobileassistant.domain.model

import com.example.mobileassistant.data.local.entity.SubGoalEntity
import java.time.LocalDateTime

data class Task(
    val id: Int,
    val title: String,
    val progress: Int,
    val isDone: Boolean,
    val completedAt: LocalDateTime?,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// Обновляем функцию toUi
fun Task.toUi(subGoalTitle: String = "", subGoalColor: Int = 0xFF4CAF50.toInt()): TaskCardUi {
    return TaskCardUi(
        id = id,
        title = title,
        progress = progress,
        note = note,
        subGoalTitle = subGoalTitle,
        subGoalColor = subGoalColor,
        formattedDate = ""
    )
}

// Новая функция для создания радар-диаграммы из подцелей
fun buildRadarFromSubGoals(
    subGoals: List<SubGoalEntity>,
    progresses: Map<Int, Int>, // subGoalId -> progress
    goalTitle: String
): RadarUi {
    if (subGoals.isEmpty()) {
        return RadarUi(emptyList(), goalTitle, 0)
    }

    val points = subGoals.mapIndexed { index, subGoal ->
        val progress = progresses[subGoal.subGoalId] ?: 0
        val angle = (360f / subGoals.size) * index

        RadarPointUi(
            label = subGoal.title,
            value = progress,
            color = subGoal.color,
            angle = angle
        )
    }

    val totalProgress = if (points.isNotEmpty()) {
        points.sumOf { it.value } / points.size
    } else {
        0
    }

    return RadarUi(points, goalTitle, totalProgress)
}

// Новая функция для создания UI подцелей
fun SubGoalEntity.toUi(progress: Int = 0, taskCount: Int = 0): SubGoalButtonUi {
    return SubGoalButtonUi(
        id = subGoalId,
        title = title,
        color = color,
        progress = progress,
        taskCount = taskCount
    )
}