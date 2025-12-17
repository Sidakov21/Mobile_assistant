package com.example.mobileassistant.data.repository

import com.example.mobileassistant.data.local.dao.GoalDao
import com.example.mobileassistant.data.local.dao.SubGoalDao
import com.example.mobileassistant.data.local.dao.TaskDao
import com.example.mobileassistant.data.local.entity.SubGoalEntity
import com.example.mobileassistant.data.local.entity.TaskEntity
import com.example.mobileassistant.data.local.entity.toDomain
import com.example.mobileassistant.domain.model.Goal
import com.example.mobileassistant.domain.model.SubGoal
import com.example.mobileassistant.domain.model.Task
import com.example.mobileassistant.domain.model.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val goalDao: GoalDao,
    private val subGoalDao: SubGoalDao,
    private val taskDao: TaskDao
) : GoalRepository {

    override fun observeTasks(goalId: Int): Flow<List<Task>> {
        return taskDao.observeTasksByGoal(goalId)
            .map { it.map { task -> task.toDomain() } }
    }

    override suspend fun getGoals(userId: Int): List<Goal> =
        goalDao.getGoalsByUser(userId).map { it.toDomain() }

    override suspend fun getGoalFull(goalId: Int): Pair<List<SubGoal>, List<Task>> {
        val data = subGoalDao.getSubGoalsWithTasks(goalId)
        return data.map { it.subGoal.toDomain() } to
                data.flatMap { it.tasks.map { t -> t.toDomain() } }
    }

    override suspend fun addSubGoal(goalId: Int, title: String) {
        subGoalDao.insertSubGoal(SubGoalEntity(goalId = goalId, title = title))
    }

    override suspend fun addTask(subGoalId: Int, title: String) {
        taskDao.insertTask(TaskEntity(subGoalId = subGoalId, title = title))
    }
}

