package com.example.mobileassistant.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mobileassistant.ui.main.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    onNavigateToSubGoals: () -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

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

    // Успешные сообщения
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToSubGoals,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_view),
                    contentDescription = "Подцели"
                )
            }
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
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
//                // Добавляем заголовок экрана
//                Text(
//                    text = "Мобильный ассистент",
//                    style = MaterialTheme.typography.headlineLarge,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp, vertical = 8.dp)
//                )

                // 1. Радар-диаграмма
                RadarChart(
                    radarData = state.radarData,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Активность за неделю
                WeeklyActivityBars(
                    activities = state.weeklyActivity,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Кнопки создания
                CreateButtonsSection(
                    onCreateGoalClick = { viewModel.showAddGoalDialog() },
                    onCreateSubGoalClick = { viewModel.showAddSubGoalDialog() },
                    isCreatingGoal = state.isCreatingGoal,
                    isCreatingSubGoal = state.isCreatingSubGoal,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
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