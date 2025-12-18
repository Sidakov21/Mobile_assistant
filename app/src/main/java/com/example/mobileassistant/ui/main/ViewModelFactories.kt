package com.example.mobileassistant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobileassistant.data.repository.GoalRepositoryImpl
import com.example.mobileassistant.ui.subgoals.SubGoalsViewModel
import com.example.mobileassistant.ui.taskdetail.TaskDetailViewModel

class MainViewModelFactory(
    private val repository: GoalRepositoryImpl
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
            return MainScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SubGoalsViewModelFactory(
    private val repository: GoalRepositoryImpl
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubGoalsViewModel::class.java)) {
            return SubGoalsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TaskDetailViewModelFactory(
    private val repository: GoalRepositoryImpl
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
            return TaskDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}