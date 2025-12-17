package com.example.mobileassistant.ui.main.repository

import com.example.mobileassistant.domain.model.Goal
import com.example.mobileassistant.domain.model.SubGoal
import com.example.mobileassistant.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    fun observeTasks(goalId: Int): Flow<List<Task>>

    suspend fun getGoals(userId: Int): List<Goal>

    suspend fun getGoalFull(goalId: Int): Pair<List<SubGoal>, List<Task>>

    suspend fun addSubGoal(goalId: Int, title: String)

    suspend fun addTask(subGoalId: Int, title: String)
}
