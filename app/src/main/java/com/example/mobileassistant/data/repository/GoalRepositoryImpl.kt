package com.example.mobileassistant.data.repository

import com.example.mobileassistant.data.local.dao.GoalDao
import com.example.mobileassistant.data.local.dao.SubGoalDao
import com.example.mobileassistant.data.local.dao.TaskDao
import com.example.mobileassistant.data.local.entity.GoalEntity
import com.example.mobileassistant.data.local.entity.SubGoalEntity
import com.example.mobileassistant.data.local.entity.TaskEntity
import com.example.mobileassistant.data.local.entity.toDomain
import com.example.mobileassistant.domain.model.Goal
import com.example.mobileassistant.domain.model.SubGoal
import com.example.mobileassistant.domain.model.Task
import com.example.mobileassistant.domain.model.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneOffset

class GoalRepositoryImpl(
    private val goalDao: GoalDao,
    private val subGoalDao: SubGoalDao,
    private val taskDao: TaskDao
) : GoalRepository {

    override fun observeGoals(userId: Int): Flow<List<Goal>> {
        return goalDao.observeGoalsByUser(userId)
            .map { goals -> goals.map { it.toDomain() } }
    }

    override fun observeTasks(goalId: Int): Flow<List<Task>> {
        return taskDao.observeTasksByGoal(goalId)
            .map { tasks -> tasks.map { it.toDomain() } }
    }

    override suspend fun getGoals(userId: Int): List<Goal> {
        return goalDao.getGoalsByUser(userId).map { it.toDomain() }
    }

    override suspend fun getGoalFull(goalId: Int): Pair<List<SubGoal>, List<Task>> {
        val subGoals = subGoalDao.getSubGoalsByGoal(goalId).map { it.toDomain() }
        val tasks = subGoals.flatMap { subGoal ->
            taskDao.getTasksBySubGoal(subGoal.id).map { it.toDomain() }
        }
        return Pair(subGoals, tasks)
    }

    override suspend fun addTask(subGoalId: Int, title: String, note: String) {
        val task = TaskEntity(
            subGoalId = subGoalId,
            title = title,
            note = note,
            progress = 0,
            isDone = false,
            createdAt = System.currentTimeMillis()
        )
        taskDao.insertTask(task)
    }

    override suspend fun addSubGoal(goalId: Int, title: String, color: Int) {
        val subGoal = SubGoalEntity(
            goalId = goalId,
            title = title,
            color = color,
            isCompleted = false,
            createdAt = System.currentTimeMillis()
        )
        subGoalDao.insertSubGoal(subGoal)
    }

    override suspend fun addGoal(userId: Int, title: String, description: String?) {
        val goal = GoalEntity(
            userId = userId,
            title = title,
            description = description,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )
        goalDao.insertGoal(goal)
    }

    override suspend fun updateTask(task: Task) {
        val taskEntity = TaskEntity(
            taskId = task.id,
            subGoalId = 0, // TODO: Нужно получить subGoalId
            title = task.title,
            progress = task.progress,
            isDone = task.isDone,
            note = task.note,
            createdAt = task.createdAt,
            completedAt = task.completedAt
        )
        taskDao.updateTask(taskEntity)
    }

    override suspend fun deleteTask(taskId: Int) {
        val task = taskDao.getTaskById(taskId)
        task?.let { taskDao.deleteTask(it) }
    }

    // ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ДЛЯ ПОДДЕРЖКИ ВСЕЙ ФУНКЦИОНАЛЬНОСТИ

    suspend fun getTask(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }

    suspend fun getSubGoal(subGoalId: Int): SubGoal? {
        return subGoalDao.getSubGoalById(subGoalId)?.toDomain()
    }

    suspend fun getGoal(goalId: Int): Goal? {
        return goalDao.getGoalById(goalId)?.toDomain()
    }

    suspend fun updateTaskProgress(taskId: Int, progress: Int) {
        taskDao.updateTaskProgress(taskId, progress)
    }

    suspend fun completeTask(taskId: Int) {
        val completedAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        taskDao.completeTask(taskId, completedAt)
    }

    suspend fun getSubGoalProgress(subGoalId: Int): Int {
        return taskDao.getSubGoalProgress(subGoalId)
    }

    suspend fun getWeeklyActivity(subGoalId: Int): Int {
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        return taskDao.getWeeklyActivity(subGoalId, sevenDaysAgo)
    }

    override suspend fun getSubGoalColor(subGoalId: Int): Int? {
        return subGoalDao.getSubGoalById(subGoalId)?.color
    }
}