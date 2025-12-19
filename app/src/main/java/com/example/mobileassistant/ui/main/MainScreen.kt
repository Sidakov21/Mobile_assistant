package com.example.mobileassistant.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.mobileassistant.ui.main.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    onNavigateToSubGoals: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showGoalsMenu by remember { mutableStateOf(false) }
    var showGoalContextMenu by remember { mutableStateOf(false) }
    var selectedGoalForContext by remember { mutableStateOf<com.example.mobileassistant.domain.model.Goal?>(null) }

    // Обработка сообщений
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                actionLabel = "OK"
            )
            viewModel.clearError()
        }
    }

    LaunchedEffect(state.showSuccessMessage) {
        state.showSuccessMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "OK"
            )
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Мобильный ассистент",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { showGoalsMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Выбор цели"
                        )
                    }

                    DropdownMenu(
                        expanded = showGoalsMenu,
                        onDismissRequest = { showGoalsMenu = false }
                    ) {
                        state.goals.forEach { goal ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = goal.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                onClick = {
                                    viewModel.selectGoal(goal.id)
                                    showGoalsMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if (state.selectedGoal?.id == goal.id) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Загрузка...")
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Текущая цель
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Текущая цель",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = state.selectedGoal?.title ?: "Не выбрана",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    state.selectedGoal?.description?.let { description ->
                                        if (description.isNotEmpty()) {
                                            Text(
                                                text = description,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }

                                FilledTonalButton(
                                    onClick = {
                                        state.selectedGoal?.let { goal ->
                                            onNavigateToSubGoals(goal.id)
                                        }
                                    },
                                    enabled = state.selectedGoal != null
                                ) {
                                    Text("Перейти")
                                }
                            }
                        }
                    }
                }

                // Радар-диаграмма
                if (state.subGoals.isNotEmpty()) {
                    item {
                        RadarChart(
                            radarData = state.radarData,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Активность за неделю
                if (state.weeklyActivity.isNotEmpty()) {
                    item {
                        WeeklyActivityBars(
                            activities = state.weeklyActivity,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Кнопки создания
                item {
                    CreateButtonsSection(
                        onCreateGoalClick = { viewModel.showAddGoalDialog() },
                        onCreateSubGoalClick = { viewModel.showAddSubGoalDialog() },
                        isCreatingGoal = state.isCreatingGoal,
                        isCreatingSubGoal = state.isCreatingSubGoal,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Список всех целей
                if (state.goals.isNotEmpty()) {
                    item {
                        Text(
                            text = "Все цели",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    items(state.goals) { goal ->
                        GoalCard(
                            goal = goal,
                            isSelected = state.selectedGoal?.id == goal.id,
                            onSelect = { viewModel.selectGoal(goal.id) },
                            onNavigate = { onNavigateToSubGoals(goal.id) },
                            onLongPress = {
                                selectedGoalForContext = goal
                                showGoalContextMenu = true
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Диалог контекстного меню для цели
        if (showGoalContextMenu && selectedGoalForContext != null) {
            GoalContextMenuDialog(
                goal = selectedGoalForContext!!,
                onComplete = {
                    viewModel.markGoalAsCompleted(selectedGoalForContext!!.id)
                    showGoalContextMenu = false
                    selectedGoalForContext = null
                },
                onDelete = {
                    viewModel.deleteGoal(selectedGoalForContext!!.id)
                    showGoalContextMenu = false
                    selectedGoalForContext = null
                },
                onDismiss = {
                    showGoalContextMenu = false
                    selectedGoalForContext = null
                }
            )
        }

        // Диалог добавления цели
        if (state.showAddGoalDialog) {
            AddGoalDialog(
                onDismiss = { viewModel.hideAddGoalDialog() },
                onConfirm = { title, description ->
                    viewModel.createNewGoal(title, description)
                },
                isCreating = state.isCreatingGoal
            )
        }

        // Диалог добавления подцели
        if (state.showAddSubGoalDialog) {
            AddSubGoalDialog(
                onDismiss = { viewModel.hideAddSubGoalDialog() },
                onConfirm = { title, color ->
                    viewModel.createNewSubGoal(title, color)
                },
                isCreating = state.isCreatingSubGoal
            )
        }
    }
}

@Composable
fun GoalCard(
    goal: com.example.mobileassistant.domain.model.Goal,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onNavigate: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder()
        } else {
            null
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    goal.description?.let { description ->
                        if (description.isNotEmpty()) {
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Выбрано",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onSelect,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Выбрать")
                }

                Button(
                    onClick = onNavigate
                ) {
                    Text("Подцели")
                }
            }
        }
    }
}

@Composable
fun GoalContextMenuDialog(
    goal: com.example.mobileassistant.domain.model.Goal,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Цель: ${goal.title}",
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Что вы хотите сделать с этой целью?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Кнопка "Отметить выполненной"
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Завершить",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Отметить выполненной")
                }

                // Кнопка "Удалить"
                Button(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Удалить цель")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Кнопка "Отмена"
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Отмена")
                }
            }
        }
    }
}