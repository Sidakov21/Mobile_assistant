package com.example.mobileassistant.ui.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobileassistant.ui.main.model.TaskUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBottomSheet(
    onAddTask: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {

            Text("Новая задача")

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название") }
            )

            Button(
                modifier = Modifier.padding(top = 16.dp),
                onClick = {
                    onAddTask(title)
                    onDismiss()
                }
            ) {
                Text("Добавить")
            }
        }
    }
}


