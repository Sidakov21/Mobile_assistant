package com.example.mobileassistant.ui.main

import MobileAssistantTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobileassistant.AppNavigation
import com.example.mobileassistant.data.repository.FakeGoalRepository
import com.example.mobileassistant.ui.subgoals.SubGoalsViewModel
import com.example.mobileassistant.ui.taskdetail.TaskDetailViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = FakeGoalRepository()

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
                    factory = TaskDetailViewModelFactory()
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

// Фабрики для ViewModel
class MainViewModelFactory(
    private val repository: com.example.mobileassistant.domain.model.repository.GoalRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
            return MainScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SubGoalsViewModelFactory(
    private val repository: com.example.mobileassistant.domain.model.repository.GoalRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubGoalsViewModel::class.java)) {
            return SubGoalsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TaskDetailViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
            return TaskDetailViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}