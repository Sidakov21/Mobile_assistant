package com.example.mobileassistant.ui.main.model

import com.example.mobileassistant.domain.model.SubGoal

data class TaskUi(
    val id: Int,
    val title: String,
    val progress: Int,
    val subGoalId: Int = 0
)