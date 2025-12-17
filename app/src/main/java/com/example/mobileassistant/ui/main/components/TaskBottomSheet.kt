package com.example.mobileassistant.ui.main.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mobileassistant.ui.main.model.TaskUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBottomSheet(
    tasks: List<TaskUi>,
    onAddTask: () -> Unit,
    onDismiss: () -> Unit
) {

    LazyColumn {
        items(tasks) { TaskCard(it) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        LazyColumn {
            items(tasks) { task ->
                TaskCard(task)
            }
        }

        Button(onClick = onAddTask) {
            Text("Добавить задачу")
        }
    }
}
