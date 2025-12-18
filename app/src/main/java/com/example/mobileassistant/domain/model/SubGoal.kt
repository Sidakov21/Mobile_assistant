package com.example.mobileassistant.domain.model

data class SubGoal(
    val id: Int,
    val title: String,
    val color: Int = 0xFF4CAF50.toInt()
)