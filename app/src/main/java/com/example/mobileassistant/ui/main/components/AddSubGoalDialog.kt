package com.example.mobileassistant.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit,
    isCreating: Boolean = false,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    val selectedColor = remember { mutableStateOf(0xFF4CAF50.toInt()) }
    val focusRequester = remember { FocusRequester() }

    val colors = listOf(
        0xFF4CAF50.toInt(),
        0xFF2196F3.toInt(),
        0xFFFF9800.toInt(),
        0xFF9C27B0.toInt(),
        0xFFF44336.toInt()
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Новая подцель",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название подцели") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    enabled = !isCreating
                )

                Text(
                    text = "Выберите цвет:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.Center)
                                    .background(
                                        color = Color(color),
                                        shape = MaterialTheme.shapes.small
                                    )
                            )

                            if (selectedColor.value == color) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .align(Alignment.Center)
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = MaterialTheme.shapes.small
                                        )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable(
                                        enabled = !isCreating,
                                        onClick = { selectedColor.value = color }
                                    )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isCreating
                    ) {
                        Text("Отмена")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank() && !isCreating) {
                                onConfirm(title, selectedColor.value)
                            }
                        },
                        enabled = title.isNotBlank() && !isCreating // ДОБАВЛЕНО
                    ) {
                        if (isCreating) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Создание...")
                            }
                        } else {
                            Text("Создать")
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}