package com.example.mobileassistant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobileassistant.ui.main.MainScreen

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object SubGoals : Screen("subgoals/{goalId}") {
        fun createRoute(goalId: Int) = "subgoals/$goalId"
    }
    object TaskDetail : Screen("taskdetail/{taskId}/{goalId}") {
        fun createRoute(taskId: Int, goalId: Int) = "taskdetail/$taskId/$goalId"
    }
}

@Composable
fun AppNavigation(
    mainViewModel: com.example.mobileassistant.ui.main.MainScreenViewModel,
    subGoalsViewModel: com.example.mobileassistant.ui.subgoals.SubGoalsViewModel,
    taskDetailViewModel: com.example.mobileassistant.ui.taskdetail.TaskDetailViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        // Главный экран
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = mainViewModel,
                onNavigateToSubGoals = {
                    mainViewModel.state.value.selectedGoal?.let { goal ->
                        navController.navigate(Screen.SubGoals.createRoute(goal.id))
                    }
                }
            )
        }

        // Экран подцелей
        composable(Screen.SubGoals.route) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId")?.toIntOrNull() ?: 1
            val currentGoalId by rememberUpdatedState(goalId)

            LaunchedEffect(currentGoalId) {
                subGoalsViewModel.loadGoalData(currentGoalId)
            }

            com.example.mobileassistant.ui.subgoals.SubGoalsScreen(
                viewModel = subGoalsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId, currentGoalId))
                }
            )
        }

        // Экран деталей задачи
        composable(Screen.TaskDetail.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            val goalId = backStackEntry.arguments?.getString("goalId")?.toIntOrNull()
            val currentTaskId by rememberUpdatedState(taskId)

            DisposableEffect(Unit) {
                onDispose {
                    taskDetailViewModel.resetState()
                }
            }

            LaunchedEffect(currentTaskId) {
                if (currentTaskId != null) {
                    taskDetailViewModel.loadTask(currentTaskId!!)
                }
            }

            val taskState by taskDetailViewModel.state.collectAsStateWithLifecycle()

            // При сохранении или удалении возвращаемся на экран подцелей
            LaunchedEffect(taskState.shouldClose) {
                if (taskState.shouldClose && goalId != null) {
                    // Обновляем главный экран
                    mainViewModel.refreshData()
                    // Обновляем экран подцелей
                    subGoalsViewModel.loadGoalData(goalId)
                    // Возвращаемся на экран подцелей
                    navController.navigate(Screen.SubGoals.createRoute(goalId)) {
                        popUpTo(Screen.SubGoals.createRoute(goalId)) {
                            inclusive = true
                        }
                    }
                }
            }

            if (currentTaskId != null) {
                com.example.mobileassistant.ui.taskdetail.TaskDetailScreen(
                    viewModel = taskDetailViewModel,
                    taskId = currentTaskId!!,
                    onNavigateBack = {
                        if (goalId != null) {
                            navController.navigate(Screen.SubGoals.createRoute(goalId)) {
                                popUpTo(Screen.SubGoals.createRoute(goalId)) {
                                    inclusive = true
                                }
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
}

// Функция для навигации с очисткой стека
fun NavController.navigateWithClearStack(route: String) {
    navigate(route) {
        popUpTo(0) // Очищаем весь стек навигации
    }
}