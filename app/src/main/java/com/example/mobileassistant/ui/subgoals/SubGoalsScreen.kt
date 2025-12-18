package com.example.mobileassistant.ui.subgoals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobileassistant.domain.model.SubGoalButtonUi
import com.example.mobileassistant.ui.main.components.AddTaskBottomSheet
import com.example.mobileassistant.ui.main.components.TaskCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubGoalsScreen(
    viewModel: SubGoalsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.goalTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.searchTasks("") }) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_search),
                            contentDescription = "Поиск"
                        )
                    }
                    IconButton(onClick = { /* TODO: Сортировка */ }) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_sort_by_size),
                            contentDescription = "Сортировка"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Кнопка 1 - Подцели (текущий экран)
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Подцели")
                    }

                    // Кнопка 2 - Статистика (заглушка)
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Статистика")
                    }

                    // Кнопка 3 - Профиль (заглушка)
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Профиль")
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddTaskDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_input_add),
                    contentDescription = "Добавить задачу"
                )
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Список подцелей (горизонтальный)
                SubGoalsList(
                    subGoals = state.subGoals,
                    selectedSubGoal = state.selectedSubGoal,
                    onSubGoalSelected = viewModel::selectSubGoal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Заголовок для списка задач
                Text(
                    text = state.selectedSubGoal?.let { "Задачи: ${it.title}" } ?: "Все задачи",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Список задач
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.filteredTasks) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onNavigateToTaskDetail(task.id) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Диалог добавления задачи
        if (state.showAddTaskDialog) {
            AddTaskBottomSheet(
                onDismiss = { viewModel.hideAddTaskDialog() },
                onConfirm = { title, note ->
                    val subGoalId = state.selectedSubGoal?.id ?: state.subGoals.firstOrNull()?.id
                    if (subGoalId != null) {
                        viewModel.addTask(subGoalId, title, note)
                    }
                }
            )
        }
    }
}

@Composable
fun SubGoalsList(
    subGoals: List<SubGoalButtonUi>,
    selectedSubGoal: SubGoalButtonUi?,
    onSubGoalSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Кнопка "Все"
        item {
            SubGoalChip(
                title = "Все",
                isSelected = selectedSubGoal == null,
                color = MaterialTheme.colorScheme.secondary,
                onClick = { onSubGoalSelected(null) }
            )
        }

        // Подцели
        items(subGoals) { subGoal ->
            SubGoalChip(
                title = subGoal.title,
                isSelected = selectedSubGoal?.id == subGoal.id,
                color = Color(subGoal.color),
                onClick = { onSubGoalSelected(subGoal.id) }
            )
        }
    }
}

@Composable
fun SubGoalChip(
    title: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else color.copy(alpha = 0.3f),
            contentColor = if (isSelected) Color.White else color
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}
