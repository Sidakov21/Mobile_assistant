package com.example.mobileassistant.ui.main

import MobileAssistantTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.mobileassistant.AppNavigation
import com.example.mobileassistant.data.local.AppDatabase
import com.example.mobileassistant.data.local.DatabaseInitializer
import com.example.mobileassistant.data.repository.GoalRepositoryImpl
import com.example.mobileassistant.ui.subgoals.SubGoalsViewModel
import com.example.mobileassistant.ui.taskdetail.TaskDetailViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем базу данных с деструктивной миграцией
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mobile-assistant-db"
        )
            .fallbackToDestructiveMigration() // ДОБАВЛЕНО
            .build()

        // Создаем репозиторий с реальной базой данных
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

class TaskDetailViewModelFactory(
    private val repository: com.example.mobileassistant.domain.model.repository.GoalRepository? = null
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
            return TaskDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}