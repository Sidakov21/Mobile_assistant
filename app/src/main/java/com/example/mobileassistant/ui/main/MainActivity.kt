package com.example.mobileassistant.ui.main

import MobileAssistantTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.room.Room.databaseBuilder
import com.example.mobileassistant.data.local.AppDatabase
import com.example.mobileassistant.data.local.entity.GoalEntity
import com.example.mobileassistant.data.repository.TaskRepositoryImpl
import kotlin.jvm.java

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Инициализируем базу данных Room
        val db = databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mobile-assistant-db"
        ).build()

        // 2. Создаем репозиторий, передавая в него DAO
        val repository = TaskRepositoryImpl(
            goalDao = db.goalDao(),
            subGoalDao = db.subGoalDao(),
            taskDao = db.taskDao()
        )
        setContent {
            MobileAssistantTheme {

                // 3. Создаем фабрику для ViewModel, чтобы передать туда репозиторий
                val viewModelFactory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                            @Suppress("UNCHECKED_CAST")
                            return MainViewModel(repository) as T

                        }
                        throw IllegalArgumentException("Unknown ViewModel class")
                    }
                }

                // 4. Передаем фабрику в функцию viewModel()
                MainScreen(
                    viewModel = viewModel(factory = viewModelFactory)
                )
            }
        }
    }
}
