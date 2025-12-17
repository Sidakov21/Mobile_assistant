package com.example.mobileassistant.ui.main.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mobileassistant.ui.main.model.TaskUi
import com.example.mobileassistant.ui.main.model.WeekActivityUi
import com.example.mobileassistant.ui.radar.RadarData

@Composable
fun TaskCard(task: TaskUi) {
    Text(task.title)
}

@Composable
fun RadarChart(data: RadarData) {}

@Composable
fun WeekActivity(data: List<WeekActivityUi>) {}

@Composable
fun TaskList(
    tasks: List<TaskUi>,
    onClick: (TaskUi) -> Unit
) {
    LazyColumn {
        items(tasks) {
            TaskCard(it)
        }
    }
}
