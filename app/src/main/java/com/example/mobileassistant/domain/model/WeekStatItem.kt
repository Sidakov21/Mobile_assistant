package com.example.mobileassistant.ui.main.model

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.semantics.SemanticsProperties.Text
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.unit.dp
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