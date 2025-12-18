package com.example.mobileassistant.ui.main.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobileassistant.domain.model.TaskCardUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
    tasks: List<TaskCardUi>,
    onAddTask: (String) -> Unit,
    onDismiss: () -> Unit,
    onTaskClick: (TaskCardUi) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        // Отображаем список задач
        tasks.forEach { task ->
            TaskCard(
                task = task,
                onClick = { onTaskClick(task) }  // Передаем onClick
            )
        }

        AddTaskButton {
            onAddTask("Новая задача")
        }
    }
}

@Composable
fun AddTaskButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Добавить задачу")
    }
}