package com.example.mobileassistant.domain.model

import com.example.mobileassistant.ui.main.model.TaskUi
import com.example.mobileassistant.ui.main.model.WeekStatItem
import com.example.mobileassistant.ui.radar.RadarData
import com.example.mobileassistant.ui.radar.RadarPoint
import java.time.DayOfWeek
import java.time.LocalDateTime

data class Task(
    val id: Int,
    val title: String,
    val progress: Int,
    val isDone: Boolean,
    val completedAt: LocalDateTime?

)

fun Task.toUi() = TaskUi(
    id = id,
    title = title,
    progress = progress
)

fun buildRadar(tasks: List<Task>): RadarData =
    RadarData(
        points = tasks.map {
            RadarPoint(it.title, it.progress)
        }
    )


fun buildWeekActivity(tasks: List<Task>): List<WeekStatItem> {
    return DayOfWeek.values().map {
        WeekStatItem(it.name, 0, 0xFF000000.toInt())
    }
}


