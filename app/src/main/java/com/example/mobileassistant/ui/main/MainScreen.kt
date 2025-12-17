package com.example.mobileassistant.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.mobileassistant.ui.main.components.RadarChart
import com.example.mobileassistant.ui.main.components.TaskBottomSheet
import com.example.mobileassistant.ui.main.components.TaskList
import com.example.mobileassistant.ui.main.components.WeekActivity

@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openBottomSheet() }
            ) {
                Text("+")
            }
        }
    ) { padding ->

        Column {
            RadarChart(state.radarData)
            WeekActivity(state.weekActivity)
            TaskList(
                tasks = state.tasks,
                onClick = { /* позже */ }
            )
        }

        if (state.isBottomSheetOpen) {
            TaskBottomSheet(
                tasks = state.tasks,
                onAddTask = { viewModel.addTask("Новая задача") },
                onDismiss = { viewModel.closeBottomSheet() }
            )
        }
    }
}


