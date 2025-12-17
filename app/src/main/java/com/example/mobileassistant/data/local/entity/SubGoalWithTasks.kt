package com.example.mobileassistant.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SubGoalWithTasks(
    @Embedded val subGoal: SubGoalEntity,
    @Relation(
        parentColumn = "subGoalId",
        entityColumn = "subGoalId"
    )
    val tasks: List<TaskEntity>
)
