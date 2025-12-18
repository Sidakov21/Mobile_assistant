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
    val tasks: List<TaskEntity>,

    @Ignore
    var progress: Int // 0-100, рассчитывается автоматически
) {
    // Конструктор без 'progress', который будет использовать Room
    constructor (subGoal: SubGoalEntity, tasks: List<TaskEntity>) : this(
        subGoal,
        tasks,
        if (tasks.isEmpty()) 0 else (tasks.count { it.isDone } * 100) / tasks.size
    )
}