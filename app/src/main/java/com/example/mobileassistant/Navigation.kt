package com.example.mobileassistant

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobileassistant.ui.main.MainScreen
import com.example.mobileassistant.ui.main.MainScreenViewModel
import com.example.mobileassistant.ui.subgoals.SubGoalsScreen
import com.example.mobileassistant.ui.subgoals.SubGoalsViewModel
import com.example.mobileassistant.ui.taskdetail.TaskDetailScreen
import com.example.mobileassistant.ui.taskdetail.TaskDetailViewModel

// Определяем маршруты навигации
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object SubGoals : Screen("subgoals")
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
                    navController.navigate(Screen.SubGoals.route)
                },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }

        // Экран подцелей
        composable(Screen.SubGoals.route) {
            SubGoalsScreen(
                viewModel = subGoalsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }

        // Экран деталей задачи
        composable(Screen.TaskDetail.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()

            if (taskId != null) {
                TaskDetailScreen(
                    viewModel = taskDetailViewModel,
                    taskId = taskId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}