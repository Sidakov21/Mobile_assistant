package com.example.mobileassistant.domain.model

data class SubGoal(
    val id: Int,
    val name: String,
    val tasks: List<TaskCard>
)