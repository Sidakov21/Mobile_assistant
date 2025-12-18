package com.example.mobileassistant.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Мобильный ассистент",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
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
                }
            )
        }

        // Диалог добавления подцели
        if (state.showAddSubGoalDialog) {
            AddSubGoalDialog(
                onDismiss = { viewModel.hideAddSubGoalDialog() },
                onConfirm = { title, color ->
                    viewModel.createNewSubGoal(title, color)
                }
            )
        }
    }
}