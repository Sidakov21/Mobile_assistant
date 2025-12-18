package com.example.mobileassistant.data.local.entity

data class TaskWithSubGoal(
    val taskId: Int,
    val title: String,
    val progress: Int,
    val note: String,
    val isDone: Boolean,
    val subGoalId: Int,
    val subGoalTitle: String,
    val subGoalColor: Int
)