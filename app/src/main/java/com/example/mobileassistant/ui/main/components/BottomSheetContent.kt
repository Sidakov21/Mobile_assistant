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
import com.example.mobileassistant.ui.main.model.TaskUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
    tasks: List<TaskUi>,
    onAddTask: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        // Отображаем список задач
        tasks.forEach { task ->
            // Убедитесь, что TaskCard тоже существует и импортирован
            // Если нет, временно закомментируйте строку ниже:
            TaskCard(task)
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

//// Заглушка для TaskCard (на случай, если её тоже нет)
//// Если она есть - удалите этот блок и добавьте import
//@Composable
//fun TaskCard(task: TaskUi) {
//    Text(
//        text = task.title,
//        modifier = Modifier.padding(16.dp)
//    )
//}
