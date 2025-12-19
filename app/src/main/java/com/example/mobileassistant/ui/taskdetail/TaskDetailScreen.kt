package com.example.mobileassistant.ui.taskdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    viewModel: TaskDetailViewModel,
    taskId: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Загружаем задачу при первом открытии экрана
    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    // Сбрасываем состояние при закрытии экрана
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }

    // Обработка ошибок
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                actionLabel = "OK"
            )
            viewModel.clearError()
        }
    }

    // Успешное сохранение
    LaunchedEffect(state.showSaveSuccess) {
        if (state.showSaveSuccess) {
            snackbarHostState.showSnackbar(
                message = "Задача сохранена",
                actionLabel = "OK"
            )
            viewModel.hideSaveSuccess()
        }
    }

    // Автоматический переход назад при удалении
    LaunchedEffect(state.shouldClose) {
        if (state.shouldClose) {
            delay(500) // Даем время показать сообщение
            onNavigateBack()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.taskId > 0) "Редактирование задачи" else "Новая задача",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = viewModel::deleteTask,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        enabled = !state.isDeleting && state.taskId > 0
                    ) {
                        if (state.isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onError
                            )
                        } else {
                            Text("Удалить")
                        }
                    }

                    Button(
                        onClick = viewModel::saveTask,
                        enabled = state.isModified && !state.isSaving,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Сохранить")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Поле ввода названия
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::updateTitle,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Название задачи") },
                    placeholder = { Text("Введите название задачи") },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium
                )

                // Прогресс
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Прогресс: ${state.progress}%",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Slider(
                        value = state.progress.toFloat(),
                        onValueChange = { viewModel.updateProgress(it.toInt()) },
                        valueRange = 0f..100f,
                        steps = 19, // 5% шаги
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Поле ввода заметки
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Заметка",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = state.note,
                        onValueChange = viewModel::updateNote,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        placeholder = { Text("Добавьте заметку...") },
                        maxLines = 10
                    )
                }
            }
        }
    }
}