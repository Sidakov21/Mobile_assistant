package com.example.mobileassistant.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobileassistant.AppNavigation
import com.example.mobileassistant.MobileAssistantTheme
import com.example.mobileassistant.data.local.DatabaseInitializer
import com.example.mobileassistant.data.repository.GoalRepositoryImpl
import com.example.mobileassistant.ui.subgoals.SubGoalsViewModel
import com.example.mobileassistant.ui.taskdetail.TaskDetailViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем базу данных
        val database = DatabaseInitializer.initialize(applicationContext)

        // Создаем репозиторий
        val repository = GoalRepositoryImpl(
            goalDao = database.goalDao(),
            subGoalDao = database.subGoalDao(),
            taskDao = database.taskDao()
        )

        setContent {
            MobileAssistantTheme {
                // Создаем ViewModel для каждого экрана
                val mainViewModel: MainScreenViewModel = viewModel(
                    factory = MainViewModelFactory(repository)
                )

                val subGoalsViewModel: SubGoalsViewModel = viewModel(
                    factory = SubGoalsViewModelFactory(repository)
                )

                val taskDetailViewModel: TaskDetailViewModel = viewModel(
                    factory = TaskDetailViewModelFactory(repository)
                )

                // Используем навигацию
                AppNavigation(
                    mainViewModel = mainViewModel,
                    subGoalsViewModel = subGoalsViewModel,
                    taskDetailViewModel = taskDetailViewModel
                )
            }
        }
    }
}