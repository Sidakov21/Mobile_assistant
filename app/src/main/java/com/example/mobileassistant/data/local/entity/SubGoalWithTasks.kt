package com.example.mobileassistant.data.local.entity

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

data class SubGoalWithTasks(
    @Embedded
    val subGoal: SubGoalEntity,

    @Relation(
        parentColumn = "subGoalId",
        entityColumn = "subGoalId"
    )
    val tasks: List<TaskEntity>
) {
    // Вычисляем прогресс вручную
    val progress: Int
        get() = if (tasks.isEmpty()) 0 else {
            val completed = tasks.count { it.isDone }
            (completed * 100) / tasks.size
        }
}