package com.example.mobileassistant.ui.subgoals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobileassistant.domain.model.SubGoalButtonUi
import com.example.mobileassistant.ui.main.components.AddTaskBottomSheet
import com.example.mobileassistant.ui.main.components.TaskCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubGoalsScreen(
    viewModel: SubGoalsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Обработка сообщений
    LaunchedEffect(state.showSuccessMessage) {
        state.showSuccessMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearSuccessMessage()
            }
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
                viewModel.clearError()
            }
        }
    }

    // Разделяем задачи
    val activeTasks = state.filteredTasks.filter { !it.isCompleted }
    val completedTasks = state.filteredTasks.filter { it.isCompleted }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Три точки (меню)
                        IconButton(
                            onClick = {
                                // Можно добавить меню настроек или фильтров
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Меню",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        // Название цели
                        Text(
                            text = state.goalTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        // Кнопка назад
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Назад",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddTaskDialog() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить задачу"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                // Поле поиска
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.searchTasks(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    label = { Text("Поиск задач") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Поиск"
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchTasks("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Очистить поиск"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )

                // Панель сортировки
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Сортировка:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    FilterChip(
                        selected = state.sortType == SortType.DATE_NEWEST,
                        onClick = { viewModel.setSortType(SortType.DATE_NEWEST) },
                        label = { Text("Новые") },
                        leadingIcon = if (state.sortType == SortType.DATE_NEWEST) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )

                    FilterChip(
                        selected = state.sortType == SortType.PROGRESS_HIGH,
                        onClick = { viewModel.setSortType(SortType.PROGRESS_HIGH) },
                        label = { Text("Прогресс") }
                    )

                    FilterChip(
                        selected = state.sortType == SortType.ALPHABETICAL,
                        onClick = { viewModel.setSortType(SortType.ALPHABETICAL) },
                        label = { Text("А-Я") }
                    )
                }

                // Список подцелей
                SubGoalsList(
                    subGoals = state.subGoals,
                    selectedSubGoal = state.selectedSubGoal,
                    onSubGoalSelected = viewModel::selectSubGoal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Счетчики задач
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Всего задач: ${state.filteredTasks.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Выполнено: ${completedTasks.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Активные задачи
                if (activeTasks.isNotEmpty()) {
                    Text(
                        text = "Активные задачи (${activeTasks.size})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(activeTasks) { task ->
                            TaskCard(
                                task = task,
                                onClick = { onNavigateToTaskDetail(task.id) }
                            )
                        }
                    }
                }

                // Завершенные задачи
                if (completedTasks.isNotEmpty()) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Text(
                        text = "Завершенные задачи (${completedTasks.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(completedTasks) { task ->
                            TaskCard(
                                task = task,
                                onClick = { onNavigateToTaskDetail(task.id) }
                            )
                        }
                    }
                }

                // Если нет задач
                if (state.filteredTasks.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = "Нет задач",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Нет задач",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        if (state.searchQuery.isNotEmpty()) {
                            Text(
                                text = "Попробуйте изменить поисковый запрос",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        } else {
                            Text(
                                text = "Нажмите на кнопку +, чтобы добавить задачу",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
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


// ДОБАВЛЯЕМ КОМПОНЕНТ SubGoalsList В КОНЕЦ ФАЙЛА:

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