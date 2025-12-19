package com.example.mobileassistant.domain.model

import com.example.mobileassistant.data.local.entity.SubGoalEntity
import com.example.mobileassistant.data.local.entity.TaskEntity
import java.text.SimpleDateFormat
import java.util.*

// Модель для карточки задачи на UI
data class TaskCardUi(
    val id: Int,
    val title: String,
    val progress: Int,
    val note: String,
    val subGoalTitle: String,
    val subGoalColor: Int,
    val formattedDate: String,
    val subGoalId: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
) {
    companion object {
        fun fromEntity(task: TaskEntity, subGoalTitle: String, subGoalColor: Int): TaskCardUi {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val date = dateFormat.format(Date(task.createdAt))

            return TaskCardUi(
                id = task.taskId,
                title = task.title,
                progress = task.progress,
                note = task.note,
                subGoalTitle = subGoalTitle,
                subGoalColor = subGoalColor,
                formattedDate = date,
                subGoalId = task.subGoalId,
                isCompleted = task.isDone,  // Используем isDone из Entity
                completedAt = task.completedAt
            )
        }
    }
}

// Модель для кнопки подцели на UI
data class SubGoalButtonUi(
    val id: Int,
    val title: String,
    val color: Int,
    val progress: Int, // 0-100
    val taskCount: Int,
    val isSelected: Boolean = false
) {
    companion object {
        fun fromEntity(subGoal: SubGoalEntity, progress: Int, taskCount: Int): SubGoalButtonUi {
            return SubGoalButtonUi(
                id = subGoal.subGoalId,
                title = subGoal.title,
                color = subGoal.color,
                progress = progress,
                taskCount = taskCount
            )
        }
    }
}

// Модель для точки на радар-диаграмме
data class RadarPointUi(
    val label: String,
    val value: Int, // 0-100
    val color: Int,
    val angle: Float // угол в градусах для отрисовки
)

// Модель для радар-диаграммы
data class RadarUi(
    val points: List<RadarPointUi>,
    val centerText: String, // название главной цели
    val totalProgress: Int // общий прогресс по всем подцелям
)


// Модель для активности за неделю (прогресс-бар)
data class WeeklyActivityUi(
    val subGoalTitle: String,
    val progress: Int, // 0-100
    val color: Int,
    val activityCount: Int // количество действий за неделю
)

// Модель для экрана подцелей
data class SubGoalsScreenState(
    val goalTitle: String,
    val subGoals: List<SubGoalButtonUi>,
    val selectedSubGoal: SubGoalButtonUi?,
    val tasks: List<TaskCardUi>,
    val isLoading: Boolean = false
)