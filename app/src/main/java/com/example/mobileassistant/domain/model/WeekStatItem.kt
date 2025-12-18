package com.example.mobileassistant.ui.main.model

import com.example.mobileassistant.domain.model.Task
import java.time.DayOfWeek

data class WeekStatItem(
    val day: String,
    val completed: Int,
    val color: Int
)

fun buildWeekActivity(tasks: List<Task>): List<WeekStatItem> {
    return DayOfWeek.entries.map {
        WeekStatItem(
            day = it.name,
            completed = 0,
            color = 0xFF000000.toInt()
        )
    }
}

fun WeekStatItem.toUi() = WeekActivityUi(
    day = day,
    completed = completed,
    color = color
)