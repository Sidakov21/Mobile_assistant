package com.example.mobileassistant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobileassistant.ui.main.MainScreen
import com.example.mobileassistant.ui.main.MainScreenViewModel
import com.example.mobileassistant.ui.subgoals.SubGoalsScreen
import com.example.mobileassistant.ui.subgoals.SubGoalsViewModel
import com.example.mobileassistant.ui.taskdetail.TaskDetailScreen
import com.example.mobileassistant.ui.taskdetail.TaskDetailViewModel

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object SubGoals : Screen("subgoals/{goalId}") {
        fun createRoute(goalId: Int) = "subgoals/$goalId"
    }
    object TaskDetail : Screen("taskdetail/{taskId}") {
        fun createRoute(taskId: Int) = "taskdetail/$taskId"
    }
}

@Composable
fun AppNavigation(
    mainViewModel: MainScreenViewModel,
    subGoalsViewModel: SubGoalsViewModel,
    taskDetailViewModel: TaskDetailViewModel
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
                },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
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

            SubGoalsScreen(
                viewModel = subGoalsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }

        // Экран деталей задачи
        composable(Screen.TaskDetail.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            val currentTaskId by rememberUpdatedState(taskId)

            DisposableEffect(Unit) {
                onDispose {
                    // Сбрасываем состояние ViewModel при закрытии экрана
                    taskDetailViewModel.resetState()
                }
            }

            LaunchedEffect(currentTaskId) {
                if (currentTaskId != null) {
                    taskDetailViewModel.loadTask(currentTaskId!!)
                }
            }

            val taskState by taskDetailViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(taskState.shouldClose) {
                if (taskState.shouldClose) {
                    navController.popBackStack()
                }
            }

            if (currentTaskId != null) {
                TaskDetailScreen(
                    viewModel = taskDetailViewModel,
                    taskId = currentTaskId!!,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}