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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GoalRepositoryImpl(
    private val goalDao: GoalDao,
    private val subGoalDao: SubGoalDao,
    private val taskDao: TaskDao
) : GoalRepository {

    // Механизм для уведомления об обновлениях данных
    private val dataUpdateMutex = Mutex()
    private val _dataUpdates = MutableSharedFlow<Unit>(replay = 1)
    override val dataUpdates: SharedFlow<Unit> = _dataUpdates.asSharedFlow()

    private suspend fun notifyDataUpdate() {
        _dataUpdates.emit(Unit)
    }

    // Получаем цели пользователя (реактивно)
    override fun observeGoals(userId: Int): Flow<List<Goal>> {
        return goalDao.observeGoalsByUser(userId)
            .map { goals -> goals.map { DomainMapper.goal.toDomain(it) } }
            .combine(dataUpdates) { goals, _ -> goals } // Обновляем при изменениях
    }

    // Получаем задачи по цели (реактивно)
    override fun observeTasks(goalId: Int): Flow<List<Task>> {
        return taskDao.observeTasksByGoal(goalId)
            .map { tasks -> tasks.map { DomainMapper.task.toDomain(it) } }
            .combine(dataUpdates) { tasks, _ -> tasks } // Обновляем при изменениях
    }

    // Получаем цели пользователя (синхронно)
    override suspend fun getGoals(userId: Int): List<Goal> {
        return dataUpdateMutex.withLock {
            goalDao.getGoalsByUser(userId).map { DomainMapper.goal.toDomain(it) }
        }
    }

    override suspend fun getGoal(goalId: Int): Goal? {
        return dataUpdateMutex.withLock {
            goalDao.getGoalById(goalId)?.let { DomainMapper.goal.toDomain(it) }
        }
    }

    // Получаем полную информацию по цели (подцели + задачи)
    override suspend fun getGoalFull(goalId: Int): Pair<List<SubGoal>, List<Task>> {
        return dataUpdateMutex.withLock {
            // Получаем подцели
            val subGoalEntities = subGoalDao.getSubGoalsByGoal(goalId)
            val subGoals = subGoalEntities.map { DomainMapper.subGoal.toDomain(it) }

            // Получаем все задачи для этих подцелей
            val tasks = subGoalEntities.flatMap { subGoal ->
                taskDao.getTasksBySubGoal(subGoal.subGoalId).map { DomainMapper.task.toDomain(it) }
            }

            Pair(subGoals, tasks)
        }
    }

    // Добавляем задачу
    override suspend fun addTask(subGoalId: Int, title: String, note: String) {
        dataUpdateMutex.withLock {
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
            notifyDataUpdate()
        }
    }

    // Добавляем подцель
    override suspend fun addSubGoal(goalId: Int, title: String, color: Int) {
        dataUpdateMutex.withLock {
            val subGoalEntity = SubGoalEntity(
                goalId = goalId,
                title = title,
                color = color,
                isCompleted = false,
                createdAt = System.currentTimeMillis()
            )
            subGoalDao.insertSubGoal(subGoalEntity)
            notifyDataUpdate()
        }
    }

    // Добавляем цель
    override suspend fun addGoal(userId: Int, title: String, description: String?) {
        dataUpdateMutex.withLock {
            val goalEntity = GoalEntity(
                userId = userId,
                title = title,
                description = description,
                isCompleted = false,
                createdAt = System.currentTimeMillis()
            )
            goalDao.insertGoal(goalEntity)
            notifyDataUpdate()
        }
    }

    // Обновляем задачу
    override suspend fun updateTask(task: Task) {
        dataUpdateMutex.withLock {
            val existingTask = taskDao.getTaskById(task.id)
            if (existingTask != null) {
                val taskEntity = TaskEntity(
                    taskId = task.id,
                    subGoalId = existingTask.subGoalId,
                    title = task.title,
                    progress = task.progress,
                    isDone = task.isDone,
                    note = task.note,
                    createdAt = task.createdAt,
                    completedAt = task.completedAt
                )
                taskDao.updateTask(taskEntity)
                notifyDataUpdate()
            } else {
                throw IllegalStateException("Задача с ID ${task.id} не найдена")
            }
        }
    }

    // Удаляем задачу
    override suspend fun deleteTask(taskId: Int) {
        dataUpdateMutex.withLock {
            val task = taskDao.getTaskById(taskId)
            task?.let {
                taskDao.deleteTask(it)
                notifyDataUpdate()
            }
        }
    }

    // Получаем цвет подцели
    override suspend fun getSubGoalColor(subGoalId: Int): Int? {
        return dataUpdateMutex.withLock {
            subGoalDao.getSubGoalColor(subGoalId)
        }
    }

    // Получаем задачу по ID
    override suspend fun getTask(taskId: Int): Task? {
        return dataUpdateMutex.withLock {
            taskDao.getTaskById(taskId)?.let { DomainMapper.task.toDomain(it) }
        }
    }

    // Получаем подцель по ID
    override suspend fun getSubGoal(subGoalId: Int): SubGoal? {
        return dataUpdateMutex.withLock {
            subGoalDao.getSubGoalById(subGoalId)?.let { DomainMapper.subGoal.toDomain(it) }
        }
    }

    // В GoalRepositoryImpl добавляем метод:
    suspend fun getAllSubGoalsWithTasks(): Map<SubGoal, List<Task>> {
        return dataUpdateMutex.withLock {
            val goals = goalDao.getGoalsByUser(1)
            val result = mutableMapOf<SubGoal, List<Task>>()

            goals.forEach { goal ->
                val (subGoals, tasks) = getGoalFull(goal.goalId)
                subGoals.forEach { subGoal ->
                    val subGoalTasks = tasks.filter { it.subGoalId == subGoal.id }
                    result[subGoal] = subGoalTasks
                }
            }

            result
        }
    }

    suspend fun updateTaskProgress(taskId: Int, progress: Int) {
        dataUpdateMutex.withLock {
            taskDao.updateTaskProgress(taskId, progress)
            notifyDataUpdate()
        }
    }

    suspend fun completeTask(taskId: Int) {
        dataUpdateMutex.withLock {
            val completedAt = System.currentTimeMillis()
            taskDao.completeTask(taskId, completedAt)
            notifyDataUpdate()
        }
    }

    suspend fun getSubGoalProgress(subGoalId: Int): Int {
        return dataUpdateMutex.withLock {
            taskDao.getSubGoalProgress(subGoalId)
        }
    }

    suspend fun getWeeklyActivity(subGoalId: Int): Int {
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        return dataUpdateMutex.withLock {
            taskDao.getWeeklyActivity(subGoalId, sevenDaysAgo)
        }
    }

    suspend fun getTaskCount(subGoalId: Int): Int {
        return dataUpdateMutex.withLock {
            taskDao.getTaskCount(subGoalId)
        }
    }

    suspend fun getCompletedTaskCount(subGoalId: Int): Int {
        return dataUpdateMutex.withLock {
            taskDao.getCompletedTaskCount(subGoalId)
        }
    }
}