package com.example.mobileassistant.data.repository

import com.example.mobileassistant.data.local.dao.GoalDao
import com.example.mobileassistant.data.local.dao.SubGoalDao
import com.example.mobileassistant.data.local.dao.TaskDao
import com.example.mobileassistant.data.local.entity.GoalEntity
import com.example.mobileassistant.data.local.entity.SubGoalEntity
import com.example.mobileassistant.data.local.entity.TaskEntity
import com.example.mobileassistant.domain.model.Goal
import com.example.mobileassistant.domain.model.SubGoal
import com.example.mobileassistant.domain.model.Task
import com.example.mobileassistant.domain.model.repository.GoalRepository
import com.example.mobileassistant.ui.main.model.DomainMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneOffset

class GoalRepositoryImpl(
    private val goalDao: GoalDao,
    private val subGoalDao: SubGoalDao,
    private val taskDao: TaskDao
) : GoalRepository {

    // Получаем цели пользователя (реактивно)
    override fun observeGoals(userId: Int): Flow<List<Goal>> {
        return goalDao.observeGoalsByUser(userId)
            .map { goals -> goals.map { DomainMapper.goal.toDomain(it) } }
    }

    // Получаем задачи по цели (реактивно)
    override fun observeTasks(goalId: Int): Flow<List<Task>> {
        return taskDao.observeTasksByGoal(goalId)
            .map { tasks -> tasks.map { DomainMapper.task.toDomain(it) } }
    }

    // Получаем цели пользователя (синхронно)
    override suspend fun getGoals(userId: Int): List<Goal> {
        return goalDao.getGoalsByUser(userId).map { DomainMapper.goal.toDomain(it) }
    }

    // Получаем полную информацию по цели (подцели + задачи)
    override suspend fun getGoalFull(goalId: Int): Pair<List<SubGoal>, List<Task>> {
        // Получаем подцели
        val subGoalEntities = subGoalDao.getSubGoalsByGoal(goalId)
        val subGoals = subGoalEntities.map { DomainMapper.subGoal.toDomain(it) }

        // Получаем все задачи для этих подцелей
        val tasks = subGoalEntities.flatMap { subGoal ->
            taskDao.getTasksBySubGoal(subGoal.subGoalId).map { DomainMapper.task.toDomain(it) }
        }

        return Pair(subGoals, tasks)
    }

    // Добавляем задачу
    override suspend fun addTask(subGoalId: Int, title: String, note: String) {
        val taskEntity = TaskEntity(
            subGoalId = subGoalId,
            title = title,
            note = note,
            progress = 0,
            isDone = false,
            createdAt = System.currentTimeMillis(),
            completedAt = null
        )
        taskDao.insertTask(taskEntity)
    }

    // Добавляем подцель
    override suspend fun addSubGoal(goalId: Int, title: String, color: Int) {
        val subGoalEntity = SubGoalEntity(
            goalId = goalId,
            title = title,
            color = color,
            isCompleted = false,
            createdAt = System.currentTimeMillis()
        )
        subGoalDao.insertSubGoal(subGoalEntity)
    }

    // Добавляем цель
    override suspend fun addGoal(userId: Int, title: String, description: String?) {
        val goalEntity = GoalEntity(
            userId = userId,
            title = title,
            description = description,
            isCompleted = false,
            createdAt = System.currentTimeMillis()
        )
        goalDao.insertGoal(goalEntity)
    }

    // Обновляем задачу
    override suspend fun updateTask(task: Task) {
        // Сначала получаем существующую задачу, чтобы не потерять subGoalId
        val existingTask = taskDao.getTaskById(task.id)
        if (existingTask != null) {
            val taskEntity = TaskEntity(
                taskId = task.id,
                subGoalId = existingTask.subGoalId, // Сохраняем оригинальный subGoalId
                title = task.title,
                progress = task.progress,
                isDone = task.isDone,
                note = task.note,
                createdAt = task.createdAt,
                completedAt = task.completedAt
            )
            taskDao.updateTask(taskEntity)
        } else {
            throw IllegalStateException("Задача с ID ${task.id} не найдена")
        }
    }

    // Удаляем задачу
    override suspend fun deleteTask(taskId: Int) {
        val task = taskDao.getTaskById(taskId)
        task?.let { taskDao.deleteTask(it) }
    }

    // Получаем цвет подцели
    override suspend fun getSubGoalColor(subGoalId: Int): Int? {
        return subGoalDao.getSubGoalColor(subGoalId)
    }

    // Получаем задачу по ID
    override suspend fun getTask(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)?.let { DomainMapper.task.toDomain(it) }
    }

    // Получаем подцель по ID
    override suspend fun getSubGoal(subGoalId: Int): SubGoal? {
        return subGoalDao.getSubGoalById(subGoalId)?.let { DomainMapper.subGoal.toDomain(it) }
    }

    // ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ДЛЯ ПОДДЕРЖКИ ВСЕЙ ФУНКЦИОНАЛЬНОСТИ

    suspend fun getGoal(goalId: Int): Goal? {
        return goalDao.getGoalById(goalId)?.let { DomainMapper.goal.toDomain(it) }
    }

    suspend fun updateTaskProgress(taskId: Int, progress: Int) {
        taskDao.updateTaskProgress(taskId, progress)
    }

    suspend fun completeTask(taskId: Int) {
        val completedAt = System.currentTimeMillis()
        taskDao.completeTask(taskId, completedAt)
    }

    suspend fun getSubGoalProgress(subGoalId: Int): Int {
        return taskDao.getSubGoalProgress(subGoalId)
    }

    suspend fun getWeeklyActivity(subGoalId: Int): Int {
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        return taskDao.getWeeklyActivity(subGoalId, sevenDaysAgo)
    }

    suspend fun getTaskCount(subGoalId: Int): Int {
        return taskDao.getTaskCount(subGoalId)
    }

    suspend fun getCompletedTaskCount(subGoalId: Int): Int {
        return taskDao.getCompletedTaskCount(subGoalId)
    }
}