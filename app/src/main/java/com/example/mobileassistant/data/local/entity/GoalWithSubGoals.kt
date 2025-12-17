package com.example.mobileassistant.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class GoalWithSubGoals(
    @Embedded val goal: GoalEntity,
    @Relation(
        parentColumn = "goalId",
        entityColumn = "goalId"
    )
    val subGoals: List<SubGoalEntity>
)